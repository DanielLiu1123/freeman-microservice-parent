package cn.liumouren.boot.db.mongodb.core;

import cn.hutool.core.util.ReflectUtil;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.CountOptions;
import com.mongodb.client.model.EstimatedDocumentCountOptions;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.*;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperationContext;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.data.mongodb.core.convert.MongoWriter;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.data.mongodb.core.mapreduce.GroupBy;
import org.springframework.data.mongodb.core.mapreduce.GroupByResults;
import org.springframework.data.mongodb.core.mapreduce.MapReduceOptions;
import org.springframework.data.mongodb.core.mapreduce.MapReduceResults;
import org.springframework.data.mongodb.core.query.*;
import org.springframework.data.util.CloseableIterator;
import org.springframework.lang.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 扩展 {@link MongoTemplate}
 * <p> 根据 collectionName 选择不同的数据源, 达到动态数据源的目的
 *
 * <p> 针对 {@link MongoTemplate} 不是 public 的方法, 我们统一使用的 hutool 反射工具类 ReflectUtil
 * <p> TODO 处于基本可用状态, 但是可优化(在运行时用到了反射), 我们是否应该把方法缓存从而提高效率 ?
 *
 * @author freeman
 * @date 2021/12/7 21:43
 */
@Getter
@Setter
@SuppressWarnings("all")
public class DynamicMongoTemplate extends MongoTemplate {

    private Map<String, MongoTemplate> dsTemplateMappings;
    private Map<String, String> collectionNameDsMappings;
    private MongoTemplate defaultTemplate;


    public DynamicMongoTemplate(MongoDatabaseFactory mongoDbFactory,
                                MongoConverter mongoConverter,
                                MongoTemplate defaultTemplate) {
        super(mongoDbFactory, mongoConverter);
        this.defaultTemplate = defaultTemplate;
    }

    @Override
    public <T> List<T> findDistinct(Query query, String field, String collectionName, Class<?> entityClass, Class<T> resultClass) {
        return choose(collectionName).findDistinct(query, field, collectionName, entityClass, resultClass);
    }

    @Override
    public <T> GeoResults<T> geoNear(NearQuery near, Class<?> domainType, String collectionName, Class<T> returnType) {
        return choose(collectionName).geoNear(near, domainType, collectionName, returnType);
    }

    @Override
    public <T> T findAndModify(Query query, UpdateDefinition update, FindAndModifyOptions options, Class<T> entityClass, String collectionName) {
        return choose(collectionName).findAndModify(query, update, options, entityClass, collectionName);
    }

    @Override
    public <S, T> T findAndReplace(Query query, S replacement, FindAndReplaceOptions options, Class<S> entityType, String collectionName, Class<T> resultType) {
        return choose(collectionName).findAndReplace(query, replacement, options, entityType, collectionName, resultType);
    }

    @Override
    public <T> T findAndRemove(Query query, Class<T> entityClass, String collectionName) {
        return choose(collectionName).findAndRemove(query, entityClass, collectionName);
    }

    @Override
    public long count(Query query, String collectionName) {
        return choose(collectionName).count(query, collectionName);
    }

    @Override
    public long count(Query query, Class<?> entityClass, String collectionName) {
        return choose(collectionName).count(query, entityClass, collectionName);
    }

    @Override
    protected long doCount(String collectionName, Document filter, CountOptions options) {
        return ReflectUtil.invoke(choose(collectionName), "doCount", collectionName, filter, options);
    }

    @Override
    public long estimatedCount(String collectionName) {
        return choose(collectionName).estimatedCount(collectionName);
    }

    @Override
    protected long doEstimatedCount(String collectionName, EstimatedDocumentCountOptions options) {
        return ReflectUtil.invoke(choose(collectionName), "doEstimatedCount", collectionName, options);
    }

    @Override
    public <T> T insert(T objectToSave, String collectionName) {
        return choose(collectionName).insert(objectToSave, collectionName);
    }

    @Override
    protected <T> T doInsert(String collectionName, T objectToSave, MongoWriter<T> writer) {
        return ReflectUtil.invoke(choose(collectionName), "doInsert", collectionName, objectToSave, writer);
    }

    @Override
    public <T> Collection<T> insert(Collection<? extends T> batchToSave, String collectionName) {
        return choose(collectionName).insert(batchToSave, collectionName);
    }

    @Override
    protected <T> Collection<T> doInsertBatch(String collectionName, Collection<? extends T> batchToSave, MongoWriter<T> writer) {
        return ReflectUtil.invoke(choose(collectionName), "doInsertBatch", collectionName, batchToSave, writer);
    }

    @Override
    public <T> MapReduceResults<T> mapReduce(Query query, String inputCollectionName, String mapFunction, String reduceFunction, Class<T> entityClass) {
        return choose(inputCollectionName).mapReduce(query, inputCollectionName, mapFunction, reduceFunction, entityClass);
    }

    @Override
    public <T> MapReduceResults<T> mapReduce(Query query, String inputCollectionName, String mapFunction, String reduceFunction, MapReduceOptions mapReduceOptions, Class<T> entityClass) {
        return choose(inputCollectionName).mapReduce(query, inputCollectionName, mapFunction, reduceFunction, mapReduceOptions, entityClass);
    }

    @Override
    public <T> List<T> mapReduce(Query query, Class<?> domainType, String inputCollectionName, String mapFunction, String reduceFunction, MapReduceOptions mapReduceOptions, Class<T> resultType) {
        return choose(inputCollectionName).mapReduce(query, domainType, inputCollectionName, mapFunction, reduceFunction, mapReduceOptions, resultType);
    }

    @Override
    public <T> CloseableIterator<T> stream(Query query, Class<T> entityType, String collectionName) {
        return choose(collectionName).stream(query, entityType, collectionName);
    }

    @Override
    protected <T> CloseableIterator<T> doStream(Query query, Class<?> entityType, String collectionName, Class<T> returnType) {
        return ReflectUtil.invoke(choose(collectionName), "doStream", query, entityType, collectionName, returnType);
    }

    @Override
    public void executeQuery(Query query, String collectionName, DocumentCallbackHandler dch) {
        choose(collectionName).executeQuery(query, collectionName, dch);
    }

    @Override
    protected void executeQuery(Query query, String collectionName, DocumentCallbackHandler documentCallbackHandler, CursorPreparer preparer) {
        ReflectUtil.invoke(choose(collectionName), "executeQuery", query, collectionName, documentCallbackHandler, preparer);
    }

    @Override
    public <T> T execute(String collectionName, CollectionCallback<T> callback) {
        return choose(collectionName).execute(collectionName, callback);
    }

    @Override
    public MongoCollection<Document> createCollection(String collectionName) {
        return choose(collectionName).createCollection(collectionName);
    }

    @Override
    public MongoCollection<Document> createCollection(String collectionName, CollectionOptions collectionOptions) {
        return choose(collectionName).createCollection(collectionName, collectionOptions);
    }

    @Override
    public MongoCollection<Document> getCollection(String collectionName) {
        return choose(collectionName).getCollection(collectionName);
    }

    @Override
    public boolean collectionExists(String collectionName) {
        return choose(collectionName).collectionExists(collectionName);
    }

    @Override
    public void dropCollection(String collectionName) {
        choose(collectionName).dropCollection(collectionName);
    }

    @Override
    public IndexOperations indexOps(String collectionName) {
        return choose(collectionName).indexOps(collectionName);
    }

    @Override
    public BulkOperations bulkOps(BulkOperations.BulkMode bulkMode, String collectionName) {
        return choose(collectionName).bulkOps(bulkMode, collectionName);
    }

    @Override
    public BulkOperations bulkOps(BulkOperations.BulkMode mode, Class<?> entityType, String collectionName) {
        return choose(collectionName).bulkOps(mode, entityType, collectionName);
    }

    @Override
    public <T> T findOne(Query query, Class<T> entityClass, String collectionName) {
        return choose(collectionName).findOne(query, entityClass, collectionName);
    }

    @Override
    public boolean exists(Query query, String collectionName) {
        return choose(collectionName).exists(query, collectionName);
    }

    @Override
    public boolean exists(Query query, Class<?> entityClass, String collectionName) {
        return choose(collectionName).exists(query, entityClass, collectionName);
    }

    @Override
    public <T> List<T> find(Query query, Class<T> entityClass, String collectionName) {
        return choose(collectionName).find(query, entityClass, collectionName);
    }

    @Override
    public <T> T findOne(Query query, Class<T> entityClass) {
        return choose(getCollectionName(entityClass)).findOne(query, entityClass);
    }

    @Nullable
    @Override
    public <T> T findById(Object id, Class<T> entityClass, String collectionName) {
        return choose(collectionName).findById(id, entityClass, collectionName);
    }

    @Override
    public <T> T save(T objectToSave, String collectionName) {
        return choose(collectionName).save(objectToSave, collectionName);
    }

    @Override
    protected <T> T doSave(String collectionName, T objectToSave, MongoWriter<T> writer) {
        return ReflectUtil.invoke(choose(collectionName), "doSave", collectionName, objectToSave, writer);
    }

    @Override
    protected Object insertDocument(String collectionName, Document document, Class<?> entityClass) {
        return ReflectUtil.invoke(choose(collectionName), "insertDocument", collectionName, document, entityClass);
    }

    @Override
    protected List<Object> insertDocumentList(String collectionName, List<Document> documents) {
        return ReflectUtil.invoke(choose(collectionName), "insertDocumentList", collectionName, documents);
    }

    @Override
    protected Object saveDocument(String collectionName, Document dbDoc, Class<?> entityClass) {
        return ReflectUtil.invoke(choose(collectionName), "saveDocument", collectionName, dbDoc, entityClass);
    }

    @Override
    public UpdateResult upsert(Query query, UpdateDefinition update, String collectionName) {
        return choose(collectionName).upsert(query, update, collectionName);
    }

    @Override
    public UpdateResult upsert(Query query, UpdateDefinition update, Class<?> entityClass, String collectionName) {
        return choose(collectionName).upsert(query, update, entityClass, collectionName);
    }

    @Override
    public UpdateResult updateFirst(Query query, UpdateDefinition update, String collectionName) {
        return choose(collectionName).updateFirst(query, update, collectionName);
    }

    @Override
    public UpdateResult updateFirst(Query query, UpdateDefinition update, Class<?> entityClass, String collectionName) {
        return choose(collectionName).updateFirst(query, update, entityClass, collectionName);
    }

    @Override
    public UpdateResult updateMulti(Query query, UpdateDefinition update, String collectionName) {
        return choose(collectionName).updateMulti(query, update, collectionName);
    }

    @Override
    public UpdateResult updateMulti(Query query, UpdateDefinition update, Class<?> entityClass, String collectionName) {
        return choose(collectionName).updateMulti(query, update, entityClass, collectionName);
    }

    @Override
    protected UpdateResult doUpdate(String collectionName, Query query, UpdateDefinition update, Class<?> entityClass, boolean upsert, boolean multi) {
        return ReflectUtil.invoke(choose(collectionName), "doUpdate", collectionName, query, update, entityClass, upsert, multi);
    }

    @Override
    public DeleteResult remove(Object object, String collectionName) {
        return choose(collectionName).remove(object, collectionName);
    }

    @Override
    public DeleteResult remove(Query query, String collectionName) {
        return choose(collectionName).remove(query, collectionName);
    }

    @Override
    public DeleteResult remove(Query query, Class<?> entityClass, String collectionName) {
        return choose(collectionName).remove(query, entityClass, collectionName);
    }

    @Override
    protected <T> DeleteResult doRemove(String collectionName, Query query, Class<T> entityClass, boolean multi) {
        return ReflectUtil.invoke(choose(collectionName), "doRemove", collectionName, query, entityClass, multi);
    }

    @Override
    public <T> List<T> findAll(Class<T> entityClass, String collectionName) {
        return choose(collectionName).findAll(entityClass, collectionName);
    }

    @Override
    public <T> MapReduceResults<T> mapReduce(String inputCollectionName, String mapFunction, String reduceFunction, Class<T> entityClass) {
        return choose(inputCollectionName).mapReduce(inputCollectionName, mapFunction, reduceFunction, entityClass);
    }

    @Override
    public <T> MapReduceResults<T> mapReduce(String inputCollectionName, String mapFunction, String reduceFunction, MapReduceOptions mapReduceOptions, Class<T> entityClass) {
        return choose(inputCollectionName).mapReduce(inputCollectionName, mapFunction, reduceFunction, mapReduceOptions, entityClass);
    }


    @Override
    public <T> GroupByResults<T> group(String inputCollectionName, GroupBy groupBy, Class<T> entityClass) {
        return choose(inputCollectionName).group(inputCollectionName, groupBy, entityClass);
    }

    @Override
    public <T> GroupByResults<T> group(Criteria criteria, String inputCollectionName, GroupBy groupBy, Class<T> entityClass) {
        return choose(inputCollectionName).group(criteria, inputCollectionName, groupBy, entityClass);
    }

    @Override
    public <O> AggregationResults<O> aggregate(TypedAggregation<?> aggregation, String inputCollectionName, Class<O> outputType) {
        return choose(inputCollectionName).aggregate(aggregation, inputCollectionName, outputType);
    }

    @Override
    public <O> AggregationResults<O> aggregate(Aggregation aggregation, String collectionName, Class<O> outputType) {
        return choose(collectionName).aggregate(aggregation, collectionName, outputType);
    }

    @Override
    public <O> CloseableIterator<O> aggregateStream(TypedAggregation<?> aggregation, String inputCollectionName, Class<O> outputType) {
        return choose(inputCollectionName).aggregateStream(aggregation, inputCollectionName, outputType);
    }

    @Override
    public <O> CloseableIterator<O> aggregateStream(Aggregation aggregation, String collectionName, Class<O> outputType) {
        return choose(collectionName).aggregateStream(aggregation, collectionName, outputType);
    }

    @Override
    public <T> List<T> findAllAndRemove(Query query, Class<T> entityClass, String collectionName) {
        return choose(collectionName).findAllAndRemove(query, entityClass, collectionName);
    }

    @Override
    protected <T> List<T> doFindAndDelete(String collectionName, Query query, Class<T> entityClass) {
        return ReflectUtil.invoke(choose(collectionName), "doFindAndDelete", collectionName, query, entityClass);
    }

    @Override
    protected <O> AggregationResults<O> aggregate(Aggregation aggregation, String collectionName, Class<O> outputType, AggregationOperationContext context) {
        return ReflectUtil.invoke(choose(collectionName), "aggregate", aggregation, collectionName, outputType, context);
    }

    @Override
    protected <O> AggregationResults<O> doAggregate(Aggregation aggregation, String collectionName, Class<O> outputType, AggregationOperationContext context) {
        return ReflectUtil.invoke(choose(collectionName), "doAggregate", aggregation, collectionName, outputType, context);
    }

    @Override
    protected <O> CloseableIterator<O> aggregateStream(Aggregation aggregation, String collectionName, Class<O> outputType, AggregationOperationContext context) {
        return ReflectUtil.invoke(choose(collectionName), "aggregateStream", aggregation, collectionName, outputType, context);
    }

    @Override
    protected <T> T maybeCallBeforeConvert(T object, String collection) {
        return ReflectUtil.invoke(choose(collection), "maybeCallBeforeConvert", object, collection);
    }

    @Override
    protected <T> T maybeCallBeforeSave(T object, Document document, String collection) {
        return ReflectUtil.invoke(choose(collection), "maybeCallBeforeSave", object, document, collection);
    }

    @Override
    protected <T> T maybeCallAfterSave(T object, Document document, String collection) {
        return ReflectUtil.invoke(choose(collection), "maybeCallAfterSave", object, document, collection);
    }

    @Override
    protected <T> T maybeCallAfterConvert(T object, Document document, String collection) {
        return ReflectUtil.invoke(choose(collection), "maybeCallAfterConvert", object, document, collection);
    }

    @Override
    protected MongoCollection<Document> doCreateCollection(String collectionName, Document collectionOptions) {
        return ReflectUtil.invoke(choose(collectionName), "doCreateCollection", collectionName, collectionOptions);
    }

    @Override
    protected <T> T doFindOne(String collectionName, Document query, Document fields, Class<T> entityClass) {
        return ReflectUtil.invoke(choose(collectionName), "doFindOne", collectionName, query, fields, entityClass);
    }

    @Override
    protected <T> T doFindOne(String collectionName, Document query, Document fields, CursorPreparer preparer, Class<T> entityClass) {
        return ReflectUtil.invoke(choose(collectionName), "doFindOne", collectionName, query, fields, preparer, entityClass);
    }

    @Override
    protected <T> List<T> doFind(String collectionName, Document query, Document fields, Class<T> entityClass) {
        return ReflectUtil.invoke(choose(collectionName), "doFind", collectionName, query, fields, entityClass);
    }

    @Override
    protected <T> List<T> doFind(String collectionName, Document query, Document fields, Class<T> entityClass, CursorPreparer preparer) {
        return ReflectUtil.invoke(choose(collectionName), "doFind", collectionName, query, fields, entityClass, preparer);
    }

    // inner interface DocumentCallback, 外部不会直接使用到
//    @Override
//    protected <S, T> List<T> doFind(String collectionName, Document query, Document fields, Class<S> entityClass, CursorPreparer preparer, MongoTemplate.DocumentCallback<T> objectCallback) {
//        return ReflectUtil.invoke(choose(collectionName), "doFind", collectionName, query, fields, entityClass, preparer, objectCallback);
//    }

    @Override
    protected <T> T doFindAndRemove(String collectionName, Document query, Document fields, Document sort, Collation collation, Class<T> entityClass) {
        return ReflectUtil.invoke(choose(collectionName), "doFindAndRemove", collectionName, query, fields, sort, collation, entityClass);
    }

    @Override
    protected <T> T doFindAndModify(String collectionName, Document query, Document fields, Document sort, Class<T> entityClass, UpdateDefinition update, FindAndModifyOptions options) {
        return ReflectUtil.invoke(choose(collectionName), "doFindAndModify", collectionName, query, fields, sort, entityClass, update, options);
    }

    @Override
    protected <T> T doFindAndReplace(String collectionName, Document mappedQuery, Document mappedFields, Document mappedSort, com.mongodb.client.model.Collation collation, Class<?> entityType, Document replacement, FindAndReplaceOptions options, Class<T> resultType) {
        return ReflectUtil.invoke(choose(collectionName), "doFindAndReplace", collectionName, mappedQuery, mappedFields, mappedSort, collation, entityType, replacement, options, resultType);
    }


    // ========= MongoOperations start ==========

    @Override
    public <T> List<T> findDistinct(Query query, String field, String collection, Class<T> resultClass) {
        return choose(collection).findDistinct(query, field, collection, resultClass);
    }

    @Override
    public <T> T findAndReplace(Query query, T replacement, String collectionName) {
        return choose(collectionName).findAndReplace(query, replacement, collectionName);
    }

    @Override
    public <T> T findAndReplace(Query query, T replacement, FindAndReplaceOptions options, String collectionName) {
        return choose(collectionName).findAndReplace(query, replacement, options, collectionName);
    }

    @Override
    public <T> T findAndReplace(Query query, T replacement, FindAndReplaceOptions options, Class<T> entityType, String collectionName) {
        return choose(collectionName).findAndReplace(query, replacement, options, entityType, collectionName);
    }

    // ========= MongoOperations end ==========


    /**
     * 根据集合名选择对应的 mongoTemplate
     * @param collectionName 集合名
     * @return {@link MongoTemplate}
     */
    public MongoTemplate choose(String collectionName) {
        if (collectionNameDsMappings.containsKey(collectionName)) {
            String ds = collectionNameDsMappings.get(collectionName);
            if (dsTemplateMappings.containsKey(ds)) {
                return dsTemplateMappings.get(ds);
            }
        }
        // 使用默认
        return defaultTemplate;
    }

    public MongoTemplate choose(Class<?> entityClass) {
        return choose(getCollectionName(entityClass));
    }

}
