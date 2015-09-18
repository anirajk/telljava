package ch.ethz.tell;

public class Schema {

    public enum FieldType {
        NOTYPE	 ((short)0),
        NULLTYPE ((short)1),
        SMALLINT ((short)2),
        INT	 ((short)3),
        BIGINT	 ((short)4),
        FLOAT	 ((short)5),
        DOUBLE	 ((short)6),
        TEXT	 ((short)7), // this is used for CHAR and VARCHAR as well
        BLOB	 ((short)8);
        private short value;

        private FieldType(short value) {
            this.value = value;
        }

        public final short toUnderlying() {
            return value;
        }

        public final void fromUnderlying(short value) {
            this.value = value;
        }
    }

    private long mImpl;

    private final native long construct();
    private final native void destruct(long ptr);

    public Schema() {
        mImpl = construct();
    }

    @Override
    protected void finalize() {
        destruct(mImpl);
    }

    public final native boolean addFieldImpl(long self, short type, String name, boolean notNull);
    public final boolean addField(FieldType type, String name, boolean notNull) {
        return addFieldImpl(mImpl, type.toUnderlying(), name, notNull);
    }

    private final native boolean allNotNullImpl(long self);
    public final boolean allNotNull() {
        return allNotNullImpl(mImpl);
    }

    private final FieldType[] fromUnderlyings(int[] un) {
        FieldType[] res = new FieldType[un.length];
        for (int i = 0; i < un.length; ++i) {
            res[i] = FieldType.values()[un[i]];
        }
        return res;
    }

    private final native int[] fixedSizeFieldsImpl(long self);
    public final FieldType[] fixedSizeFields() {
        return fromUnderlyings(fixedSizeFieldsImpl(mImpl));
    }

    private final native int[] variableSizedFieldsImpl(long self);
    public final FieldType[] variableSizedFields() {
        return fromUnderlyings(variableSizedFieldsImpl(mImpl));
    }

}
