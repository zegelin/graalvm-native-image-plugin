/*
 * Copyright 2020 Shinya Mochida
 *
 * Licensed under the Apache License,Version2.0(the"License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,software
 * Distributed under the License is distributed on an"AS IS"BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mikeneck.graalvm.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.TreeSet;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JniConfigTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final TestJsonReader reader = new TestJsonReader();

    @Test
    void jsonWithContents() throws IOException {
        try (InputStream inputStream = reader.configJsonResource("config/jni-config-1.json")) {
            JniConfig jniConfig = objectMapper.readValue(inputStream, JniConfig.class);
            assertThat(jniConfig).contains(
                    new ClassUsage(
                            IllegalArgumentException.class, 
                            new MethodUsage("<init>", "java.lang.String")),
                    new ClassUsage(
                            ArrayList.class, 
                            new MethodUsage("<init>"), 
                            MethodUsage.of("add", Object.class))
            );
        }
    }

    @Test
    void jsonWithoutContents() throws IOException {
        try (InputStream inputStream = reader.configJsonResource("config/jni-config-2.json")) {
            JniConfig jniConfig = objectMapper.readValue(inputStream, JniConfig.class);
            assertThat(jniConfig).isEqualTo(Collections.emptySortedSet());
        }
    }

    @Test
    void mergeWithOther() {
        JniConfig left = new JniConfig(
                new ClassUsage(ArrayList.class, MethodUsage.of("<init>", int.class)),
                new ClassUsage("com.example.App", new MethodUsage("<init>")));
        JniConfig right = new JniConfig(
                new ClassUsage(IllegalArgumentException.class, MethodUsage.of("<init>", String.class)));

        JniConfig jniConfig = left.mergeWith(right);

        assertThat(jniConfig).contains(
                new ClassUsage("com.example.App", new MethodUsage("<init>")),
                new ClassUsage(IllegalArgumentException.class, MethodUsage.of("<init>", String.class)),
                new ClassUsage(ArrayList.class, MethodUsage.of("<init>", int.class)));
    }


    @Test
    void mergeWithOtherHavingSameClass() {
        JniConfig left = new JniConfig(
                new ClassUsage(ArrayList.class, MethodUsage.of("<init>", int.class)),
                new ClassUsage("com.example.App", new MethodUsage("<init>")));
        JniConfig right = new JniConfig(
                new ClassUsage(ArrayList.class, MethodUsage.of("<init>", int.class)),
                new ClassUsage(IllegalArgumentException.class, MethodUsage.of("<init>", String.class)),
                new ClassUsage("com.example.App", MethodUsage.of("run")));

        JniConfig jniConfig = left.mergeWith(right);

        assertThat(jniConfig).contains(
                new ClassUsage("com.example.App", new MethodUsage("<init>"), MethodUsage.of("run")),
                new ClassUsage(IllegalArgumentException.class, MethodUsage.of("<init>", String.class)),
                new ClassUsage(ArrayList.class, MethodUsage.of("<init>", int.class)));
    }

    @Test
    void mergeWithAlreadyMerged() {
        JniConfig left = new JniConfig(
                new ClassUsage(ArrayList.class, MethodUsage.of("<init>", int.class)),
                new ClassUsage("com.example.App", new MethodUsage("<init>")));
        JniConfig right = new JniConfig(
                new ClassUsage(ArrayList.class, MethodUsage.of("<init>", int.class)),
                new ClassUsage(IllegalArgumentException.class, MethodUsage.of("<init>", String.class), MethodUsage.of("getCause")),
                new ClassUsage("com.example.App", new TreeSet<>(
                        Arrays.asList(MethodUsage.of("run"), MethodUsage.of("start", int.class))),
                        Collections.emptySortedSet(),
                        false, false, true, false, true, false, false, false));

        JniConfig jniConfig = left.mergeWith(right);

        assertThat(jniConfig).contains(
                new ClassUsage("com.example.App", new TreeSet<>(
                        Arrays.asList(MethodUsage.of("run"), MethodUsage.of("<init>"), MethodUsage.of("start", int.class))), 
                        Collections.emptySortedSet(),
                        false, false, true, false, true, false, false, false),
                new ClassUsage(IllegalArgumentException.class, MethodUsage.of("<init>", String.class), MethodUsage.of("getCause")),
                new ClassUsage(ArrayList.class, MethodUsage.of("<init>", int.class)));
    }
}
