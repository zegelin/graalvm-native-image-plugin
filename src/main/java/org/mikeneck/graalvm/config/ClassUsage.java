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

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ClassUsage implements Comparable<ClassUsage>, MergeableConfig<ClassUsage> {

    @NotNull
    public String name = "";

    @NotNull
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public SortedSet<MethodUsage> methods = Collections.emptySortedSet();

    @NotNull
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public SortedSet<FieldUsage> fields = Collections.emptySortedSet();


    public boolean allDeclaredFields;
    public boolean allPublicFields;

    public boolean allDeclaredMethods;
    public boolean allPublicMethods;

    public boolean allDeclaredConstructors;
    public boolean allPublicConstructors;

    public boolean allDeclaredClasses;
    public boolean allPublicClasses;

    public ClassUsage() {
    }

    public ClassUsage(
            @NotNull String name,
            @NotNull SortedSet<MethodUsage> methods,
            @NotNull SortedSet<FieldUsage> fields, 
            boolean allDeclaredFields, boolean allPublicFields,
            boolean allDeclaredMethods, boolean allPublicMethods,
            boolean allDeclaredConstructors, boolean allPublicConstructors,
            boolean allDeclaredClasses, boolean allPublicClasses) {
        this.name = name;

        this.methods = methods;
        this.fields = fields;

        this.allDeclaredFields = allDeclaredFields;
        this.allPublicFields = allPublicFields;

        this.allDeclaredMethods = allDeclaredMethods;
        this.allPublicMethods = allPublicMethods;

        this.allDeclaredConstructors = allDeclaredConstructors;
        this.allPublicConstructors = allPublicConstructors;

        this.allDeclaredClasses = allDeclaredClasses;
        this.allPublicClasses = allPublicClasses;
    }

    ClassUsage(@NotNull String name) {
        this.name = name;
    }

    ClassUsage(@NotNull Class<?> klass, MethodUsage... methods) {
        this(klass.getCanonicalName(), methods);
    }

    ClassUsage(@NotNull String name, MethodUsage... methods) {
        this.name = name;
        this.methods = new TreeSet<>(Arrays.asList(methods));
    }

    ClassUsage(@NotNull String name, FieldUsage... fields) {
        this.name = name;
        this.fields = new TreeSet<>(Arrays.asList(fields));
    }

    ClassUsage(
            @NotNull String name,
            boolean allDeclaredMethods,
            boolean allDeclaredConstructors) {
        this.name = name;
        this.allDeclaredMethods = allDeclaredMethods;
        this.allDeclaredConstructors = allDeclaredConstructors;
    }

    ClassUsage(
            @NotNull String name,
            boolean allDeclaredMethods,
            boolean allDeclaredConstructors,
            boolean allDeclaredFields) {
        this.name = name;
        this.allDeclaredFields = allDeclaredFields;
        this.allDeclaredMethods = allDeclaredMethods;
        this.allDeclaredConstructors = allDeclaredConstructors;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final ClassUsage that = (ClassUsage) o;
        return allDeclaredFields == that.allDeclaredFields &&
                allPublicFields == that.allPublicFields &&
                allDeclaredMethods == that.allDeclaredMethods &&
                allPublicMethods == that.allPublicMethods &&
                allDeclaredConstructors == that.allDeclaredConstructors &&
                allPublicConstructors == that.allPublicConstructors &&
                allDeclaredClasses == that.allDeclaredClasses &&
                allPublicClasses == that.allPublicClasses &&
                name.equals(that.name) &&
                methods.equals(that.methods) &&
                fields.equals(that.fields);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, methods, fields,
                allDeclaredFields, allPublicFields,
                allDeclaredMethods, allPublicMethods,
                allDeclaredConstructors, allPublicConstructors,
                allDeclaredClasses, allPublicClasses);
    }

    @Override
    public String toString() {
        return "ClassUsage{" +
                "name='" + name + '\'' +
                ", methods=" + methods +
                ", fields=" + fields +
                ", allDeclaredFields=" + allDeclaredFields +
                ", allPublicFields=" + allPublicFields +
                ", allDeclaredMethods=" + allDeclaredMethods +
                ", allPublicMethods=" + allPublicMethods +
                ", allDeclaredConstructors=" + allDeclaredConstructors +
                ", allPublicConstructors=" + allPublicConstructors +
                ", allDeclaredClasses=" + allDeclaredClasses +
                ", allPublicClasses=" + allPublicClasses +
                '}';
    }

    @Override
    public int compareTo(@NotNull ClassUsage o) {
        return this.name.compareTo(o.name);
    }

    @Override
    public ClassUsage mergeWith(ClassUsage other) {
        if (!this.name.equals(other.name)) {
            throw new IllegalArgumentException("Cannot merge class named " + this.name + " with " + other.name + ".");
        }

        boolean allDeclaredFields = this.allDeclaredFields || other.allDeclaredFields;
        boolean allPublicFields = this.allPublicFields || other.allPublicFields;

        boolean allDeclaredMethods = this.allDeclaredMethods || other.allDeclaredMethods;
        boolean allPublicMethods = this.allPublicMethods || other.allPublicMethods;

        boolean allDeclaredConstructors = this.allDeclaredConstructors || other.allDeclaredConstructors;
        boolean allPublicConstructors = this.allPublicConstructors || other.allPublicConstructors;

        boolean allDeclaredClasses = this.allDeclaredClasses || other.allDeclaredClasses;
        boolean allPublicClasses = this.allPublicClasses || other.allPublicClasses;

        TreeSet<MethodUsage> newMethods = new TreeSet<>(this.methods);
        newMethods.addAll(other.methods);

        TreeSet<FieldUsage> newFields = new TreeSet<>(this.fields);
        newFields.addAll(other.fields);

        return new ClassUsage(this.name, newMethods, newFields,
                allDeclaredFields, allPublicFields,
                allDeclaredMethods, allPublicMethods,
                allDeclaredConstructors, allPublicConstructors,
                allDeclaredClasses, allPublicClasses);
    }
}
