#!/bin/bash
# 项目根目录执行 bash $(find . -name "generate.sh")

# 变量
namespace="default"
serviceId="unknown"
#largest_dir=$(du -sh ./* | sort -rn | head -n 1)
#server_dir=${largest_dir#*./}
# 只有一个模块的情况
server_dir="./"
# 多模板情况
if [ $(ls | grep *-server) ]; then
  server_dir=$(ls | grep *-server)
fi
# 自定义启动类目录
if [ "$1" ]; then
  server_dir="$1"
fi

k8s_maven_plugin_args=""
for line in $(cat "${server_dir}/k8s/base.properties"); do
  #  echo ${line#*=}  # 左到右截取 =
  #  echo ${line%=*}  # 右到左截取 =
  if [ "${line%=*}" == 'namespace' ]; then
    namespace=${line#*=}
  fi
  if [ "${line%=*}" == 'serviceId' ]; then
    serviceId=${line#*=}
  fi
  k8s_maven_plugin_args="${k8s_maven_plugin_args} -D${line%=*}=${line#*=}"
done

echo "================"
echo "mvn clean ......"
echo "================"
mvn clean

echo "================"
echo "下载 svc,deploy 模板......"
echo "================"
if [ ! -d "${server_dir}/src/main/jkube" ]; then
  mkdir -p "${server_dir}/src/main/jkube"
fi
if [ ! -f "${server_dir}/src/main/jkube/service.yml" ]; then
  curl https://gitee.com/danielliu1123223/freeman-config/raw/master/service.yml >"${server_dir}/src/main/jkube/service.yml"
fi
if [ ! -f "${server_dir}/src/main/jkube/deployment.yml" ]; then
  curl https://gitee.com/danielliu1123223/freeman-config/raw/master/deployment.yml >"${server_dir}/src/main/jkube/deployment.yml"
fi

echo "================"
echo "下载 Dockerfile 模板......"
echo "================"
if [ "$(find . -type f -name 'Dockerfile')" ]; then
  mv "$(find . -type f -name 'Dockerfile')" "$server_dir"/Dockerfile
else
  curl https://gitee.com/danielliu1123223/freeman-config/raw/master/Dockerfile >"$server_dir"/Dockerfile
fi

echo "================"
echo "mvn package......"
echo "================"
mvn package -Dmaven.source.skip=true -Dmaven.test.skip=true || exit

# 下面的操作都在 server 文件夹, 进入 server 文件夹,
# 40K ./freeman-order-server 截取 ./ 得到最大的文件/文件夹
cd "$server_dir" || exit
# cd $(ls | grep *-server)

echo "================"
echo "制作镜像......"
echo "================"
mvn k8s:build || exit

echo "================"
echo "上传镜像......"
echo "================"
mvn k8s:push || exit

echo "================"
echo "生成 k8s 资源文件......"
echo "================"
mvn k8s:resource $k8s_maven_plugin_args || exit

echo "================"
echo "生成 k8s 实例......"
echo "================"
mvn k8s:apply || exit

echo "执行完成!"
echo "kubectl delete svc,deploy $serviceId -n $namespace"
echo "kubectl get svc -n $namespace"
echo "kubectl get po -n $namespace"
