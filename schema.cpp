/*
 * (C) Copyright 2015 ETH Zurich Systems Group (http://www.systems.ethz.ch/) and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     Markus Pilman <mpilman@inf.ethz.ch>
 *     Simon Loesing <sloesing@inf.ethz.ch>
 *     Thomas Etter <etterth@gmail.com>
 *     Kevin Bocksrocker <kevin.bocksrocker@gmail.com>
 *     Lucas Braun <braunl@inf.ethz.ch>
 */
#include <ch_ethz_tell_Schema.h>
#include <tellstore/Record.hpp>
#include <crossbow/enum_underlying.hpp>

#include "helpers.hpp"

jlong Java_ch_ethz_tell_Schema_construct(JNIEnv*, jobject) {
    return reinterpret_cast<long>(new tell::store::Schema());
}

void Java_ch_ethz_tell_Schema_destruct(JNIEnv*, jobject, jlong self) {
    delete reinterpret_cast<tell::store::Schema*>(self);
}

jboolean Java_ch_ethz_tell_Schema_addFieldImpl(JNIEnv* env, jobject, jlong self, jshort type, jstring jname, jboolean notNull) {
    auto s = reinterpret_cast<tell::store::Schema*>(self);
    crossbow::string name = to_string(env, jname);
    uint16_t ftU = type;
    return s->addField(crossbow::from_underlying<tell::store::FieldType>(ftU), name, notNull);
}

jboolean Java_ch_ethz_tell_Schema_allNotNullImpl(JNIEnv*, jobject, jlong self) {
    auto s = reinterpret_cast<tell::store::Schema*>(self);
    return s->allNotNull();
}

namespace {
jintArray toIntArray(JNIEnv* env, const std::vector<tell::store::Field>& fields) {
    auto res = env->NewIntArray(fields.size());
    std::unique_ptr<jint[]> tmp(new jint[fields.size()]);
    for (unsigned i = 0; i < fields.size(); ++i) {
        tmp[i] = crossbow::to_underlying(fields[i].type());
    }
    env->SetIntArrayRegion(res, 0, fields.size(), tmp.get());
    return res;
}

}

jintArray Java_ch_ethz_tell_Schema_fixedSizeFieldsImpl(JNIEnv* env, jobject, jlong self) {
    auto s = reinterpret_cast<tell::store::Schema*>(self);
    return toIntArray(env, s->fixedSizeFields());
}

jintArray Java_ch_ethz_tell_Schema_variableSizedFieldsImpl(JNIEnv* env, jobject, jlong self) {
    auto s = reinterpret_cast<tell::store::Schema*>(self);
    return toIntArray(env, s->varSizeFields());
}

jshort Java_ch_ethz_tell_Schema_idOfImpl (JNIEnv* env, jobject, jlong self, jstring columnName) {
    auto s = reinterpret_cast<tell::store::Schema*>(self);
    return s->idOf(to_string(env, columnName));
}

