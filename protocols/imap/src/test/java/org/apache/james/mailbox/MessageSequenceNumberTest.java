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

package org.apache.james.mailbox;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

class MessageSequenceNumberTest {

    @Test
    void ofShouldThrowOnNegative() {
        assertThatThrownBy(() -> MessageSequenceNumber.of(-1)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void ofShouldNotThrowOnZero() {
        assertThatCode(() -> MessageSequenceNumber.of(0)).doesNotThrowAnyException();
    }

    @Test
    void ofShouldNotThrowOnPositiveValue() {
        assertThatCode(() -> MessageSequenceNumber.of(12)).doesNotThrowAnyException();
    }

    @Test
    void asIntShouldReturnValue() {
        assertThat(MessageSequenceNumber.of(12).asInt()).isEqualTo(12);
    }

    @Test
    void shouldRespectBeanContract() {
        EqualsVerifier.forClass(MessageSequenceNumber.class).verify();
    }

}