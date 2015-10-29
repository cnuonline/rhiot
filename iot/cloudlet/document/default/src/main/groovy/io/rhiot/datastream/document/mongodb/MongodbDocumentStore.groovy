/**
 * Licensed to the Rhiot under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.rhiot.datastream.document.mongodb;

import com.github.camellabs.iot.cloudlet.document.driver.mongodb.MongoQueryBuilder;
import com.mongodb.BasicDBObject
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import io.rhiot.thingsdata.CountByQueryOperation;
import io.rhiot.thingsdata.CountOperation;
import io.rhiot.datastream.document.DocumentStore;
import io.rhiot.thingsdata.FindByQueryOperation;
import io.rhiot.thingsdata.FindOneOperation
import io.rhiot.thingsdata.RemoveOperation;
import io.rhiot.thingsdata.SaveOperation;
import org.bson.types.ObjectId;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Collections.emptyMap;

public class MongodbDocumentStore implements DocumentStore {

    private static final def MONGO_ID = '_id'

    private final String documentsDbName

    private final Mongo mongo

    MongodbDocumentStore(Mongo mongo, String documentsDbName) {
        this.mongo = mongo
        this.documentsDbName = documentsDbName
    }

    @Override
    String save(SaveOperation saveOperation) {
        def document = canonicalToMongo(new BasicDBObject(saveOperation.pojo()))
        collection(saveOperation.collection()).save(document)
        document.get(MONGO_ID).toString()
    }

    @Override
    List<Map<String,Object>> findByQuery(FindByQueryOperation findByQueryOperation) {
        def universalQuery = (Map<String, Object>) findByQueryOperation.queryBuilder().getOrDefault("query", emptyMap());
        DBObject mongoQuery = new MongoQueryBuilder().jsonToMongoQuery(new BasicDBObject(universalQuery));
        int skip = ((int) findByQueryOperation.queryBuilder().getOrDefault("page", 0)) * ((int) findByQueryOperation.queryBuilder().getOrDefault("size", 100));
        DBCursor results = collection(findByQueryOperation.collection()).find(mongoQuery).
                limit((Integer) findByQueryOperation.queryBuilder().getOrDefault("size", 100)).skip(skip).sort(new MongoQueryBuilder().queryBuilderToSortConditions(findByQueryOperation.queryBuilder()));
        results.toArray().collect{ mongoToCanonical(it).toMap() }
    }

    @Override
    public long countByQuery(CountByQueryOperation findByQueryOperation) {
        Map<String, Object> universalQuery = (Map<String, Object>) findByQueryOperation.queryBuilder().getOrDefault("query", emptyMap());
        DBObject mongoQuery = new MongoQueryBuilder().jsonToMongoQuery(new BasicDBObject(universalQuery));
        int skip = ((int) findByQueryOperation.queryBuilder().getOrDefault("page", 0)) * ((int) findByQueryOperation.queryBuilder().getOrDefault("size", 100));
        DBCursor results = collection(findByQueryOperation.collection()).find(mongoQuery).
                limit((Integer) findByQueryOperation.queryBuilder().getOrDefault("size", 100)).skip(skip).sort(new MongoQueryBuilder().queryBuilderToSortConditions(findByQueryOperation.queryBuilder()));
        return results.count();
    }

    @Override
    long count(CountOperation countOperation) {
        collection(countOperation.collection()).count()
    }

    @Override
    public Map<String, Object> findOne(FindOneOperation findOneOperation) {
        ObjectId id = new ObjectId(findOneOperation.id());
        DBObject document = mongo.getDB(documentsDbName).getCollection(findOneOperation.collection()).findOne(id);
        if(document == null) {
            return null;
        }
        return mongoToCanonical(document).toMap();
    }

    @Override
    void remove(RemoveOperation removeOperation) {
        collection(removeOperation.collection()).remove(new BasicDBObject(MONGO_ID, new ObjectId(removeOperation.id())))
    }

    // Helpers

    private DBCollection collection(String collection) {
        mongo.getDB(documentsDbName).getCollection(collection)
    }

    public static DBObject canonicalToMongo(DBObject json) {
        checkNotNull(json, "JSON passed to the conversion can't be null.");
        DBObject bson = new BasicDBObject(json.toMap());
        Object id = bson.get("id");
        if (id != null) {
            bson.removeField("id");
            bson.put("_id", new ObjectId(id.toString()));
        }
        return bson;
    }

    public static DBObject mongoToCanonical(DBObject bson) {
        checkNotNull(bson, "BSON passed to the conversion can't be null.");
//        LOG.debug("Converting BSON object to JSON: {}", bson);
        DBObject json = new BasicDBObject(bson.toMap());
        Object id = json.get("_id");
        if (id != null) {
            json.removeField("_id");
            json.put("id", id.toString());
        }
        return json;
    }

}