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
package ch.ethz.tell;

public interface PredicateType {
    enum Type {
        Bool,
        Short,
        Int,
        Long,
        Float,
        Double,
        String,
        ByteArray
    }

    public Type getType();

    public <T> T value();

    public static PredicateType create(short value) {
        return new ShortType(value);
    }
    public static PredicateType create(int value) {
        return new IntType(value);
    }
    public static PredicateType create(long value) {
        return new LongType(value);
    }
    public static PredicateType create(float value) {
        return new FloatType(value);
    }
    public static PredicateType create(double value) {
        return new DoubleType(value);
    }
    public static PredicateType create(String value) {
        return new StringType(value);
    }
    public static PredicateType create(byte[] value) {
        return new ByteArrayType(value);
    }

    public class BoolType implements PredicateType {

        private boolean val;

        BoolType(boolean val) {
            this.val = val;
        }

        @Override
        public PredicateType.Type getType() {
            return Type.Bool;
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T> T value() {
            return (T) new Boolean(val);
        }
    }

    public class ShortType implements PredicateType {

        private short val;

        ShortType(short val) {
            this.val = val;
        }

        @Override
        public PredicateType.Type getType() {
            return Type.Short;
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T> T value() {
            return (T) new Short(val);
        }
    }

    public class IntType implements PredicateType {
        private int value;

        public IntType(int value) {
            this.value = value;
        }

        @Override
        public Type getType() {
            return Type.Int;
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T> T value() {
            return (T)new Integer(value);
        }
    }

    public class LongType implements PredicateType {
        private long value;

        public LongType(long value) {
            this.value = value;
        }

        @Override
        public Type getType() {
            return Type.Long;
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T> T value() {
            return (T)new Long(value);
        }
    }

    public class FloatType implements PredicateType {
        private float value;

        public FloatType(float value) {
            this.value = value;
        }

        @Override
        public Type getType() {
            return Type.Float;
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T> T value() {
            return (T)new Float(value);
        }
    }

    public class DoubleType implements PredicateType {
        private double value;

        public DoubleType(double value) {
            this.value = value;
        }

        @Override
        public Type getType() {
            return Type.Double;
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T> T value() {
            return (T)new Double(value);
        }
    }

    public class StringType implements PredicateType {
        private String value;

        public StringType(String value) {
            this.value = value;
        }

        @Override
        public Type getType() {
            return Type.String;
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T> T value() {
            return (T)new String(value);
        }
    }

    public class ByteArrayType implements PredicateType {
        private byte[] value;

        public ByteArrayType(byte[] value) {
            this.value = value;
        }

        @Override
        public Type getType() {
            return Type.ByteArray;
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T> T value() {
            return (T)value;
        }
    }
}
