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

package org.apache.james.jmap.cassandra;

import static org.apache.james.CassandraJamesServerMain.ALL_BUT_JMX_CASSANDRA_MODULE;
import static org.apache.james.jmap.TestingConstants.DOMAIN;
import static org.apache.james.jmap.TestingConstants.LOCALHOST_IP;
import static org.apache.james.jmap.TestingConstants.jmapRequestSpecBuilder;
import static org.assertj.core.api.Assertions.assertThat;

import org.apache.commons.net.imap.IMAPClient;
import org.apache.james.CassandraExtension;
import org.apache.james.DockerElasticSearchExtension;
import org.apache.james.GuiceJamesServer;
import org.apache.james.JamesServerBuilder;
import org.apache.james.JamesServerExtension;
import org.apache.james.backends.cassandra.DockerCassandraExtension;
import org.apache.james.jmap.draft.JmapGuiceProbe;
import org.apache.james.mailbox.extractor.TextExtractor;
import org.apache.james.mailbox.store.search.PDFTextExtractor;
import org.apache.james.modules.TestJMAPServerModule;
import org.apache.james.modules.protocols.ImapGuiceProbe;
import org.apache.james.utils.DataProbeImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import io.restassured.RestAssured;

class CassandraImapErrorTest {
    private static final String username = "username@" + DOMAIN;
    private static final String PASSWORD = "password";
    private static final long LIMIT_TO_10_MESSAGES = 10;
    private final CassandraExtension cassandraExtension = new CassandraExtension();

    @RegisterExtension
    JamesServerExtension serverExtension = new JamesServerBuilder()
        .extension(new DockerElasticSearchExtension())
        .extension(cassandraExtension)
        .server(configuration -> GuiceJamesServer.forConfiguration(configuration)
            .combineWith(ALL_BUT_JMX_CASSANDRA_MODULE)
            .overrideWith(binder -> binder.bind(TextExtractor.class).to(PDFTextExtractor.class))
            .overrideWith(new TestJMAPServerModule(LIMIT_TO_10_MESSAGES)))
        .build();

    @BeforeEach
    void setup(GuiceJamesServer server) throws Exception {
        RestAssured.requestSpecification = jmapRequestSpecBuilder
            .setPort(server.getProbe(JmapGuiceProbe.class).getJmapPort())
            .build();
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();

        server.getProbe(DataProbeImpl.class)
            .fluent()
            .addDomain(DOMAIN)
            .addUser(username, PASSWORD);
    }

    @Test
    @Disabled
    void causingMajorIssueDuringIMAPSessionShouldEndWithNo(GuiceJamesServer server) throws Exception {
        IMAPClient imapClient = new IMAPClient();
        try {
            imapClient.connect(LOCALHOST_IP, server.getProbe(ImapGuiceProbe.class).getImapPort());
            imapClient.login(username, PASSWORD);
            cassandraExtension.pause();

            Thread.sleep(100);

            boolean isSelected = imapClient.select("INBOX");
            assertThat(isSelected).isFalse();
            assertThat(imapClient.getReplyString()).startsWith("NO ");
        } finally {
            imapClient.disconnect();
            cassandraExtension.unpause();
        }
    }

}