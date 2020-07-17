/****************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one   *
 * or more contributor license agreements.  See the NOTICE file *
 * distributed with this work for additional information        *
 * regarding copyright ownership.  The ASF licenses this file   *
 * to you under the Apache License, Version 2.0 (the            *
 * "License"); you may not use this file except in compliance   *
 * with the License.  You may obtain a copy of the License at   *
 *                                                              *
 *   http://www.apache.org/licenses/LICENSE-2.0                 *
 *                                                              *
 * Unless required by applicable law or agreed to in writing,   *
 * software distributed under the License is distributed on an  *
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY       *
 * KIND, either express or implied.  See the License for the    *
 * specific language governing permissions and limitations      *
 * under the License.                                           *
 ****************************************************************/

package org.apache.james.blob.cassandra;

import org.apache.james.backends.cassandra.init.configuration.CassandraConfiguration;
import org.apache.james.blob.api.BlobStore;
import org.apache.james.blob.api.BucketName;
import org.apache.james.blob.api.HashBlobId;
import org.apache.james.server.blob.deduplication.DeDuplicationBlobStore;

import com.datastax.driver.core.Session;

public class CassandraBlobStoreFactory {
    public static BlobStore forTesting(HashBlobId.Factory blobIdFactory,
                       BucketName defaultBucketName,
                       CassandraDumbBlobStore dumbBlobStore) {
        return new DeDuplicationBlobStore(dumbBlobStore, defaultBucketName, blobIdFactory);
    }

    public static BlobStore forTesting(Session session) {
        HashBlobId.Factory blobIdFactory = new HashBlobId.Factory();
        CassandraBucketDAO bucketDAO = new CassandraBucketDAO(blobIdFactory, session);
        CassandraDefaultBucketDAO defaultBucketDAO = new CassandraDefaultBucketDAO(session);
        return forTesting(
            blobIdFactory,
            BucketName.DEFAULT,
            new CassandraDumbBlobStore(defaultBucketDAO, bucketDAO, CassandraConfiguration.DEFAULT_CONFIGURATION, BucketName.DEFAULT));
    }
}
