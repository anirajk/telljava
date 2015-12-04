package ch.ethz.tell;

import java.io.UnsupportedEncodingException;

import ch.ethz.tell.Field.FieldType;

/**
 */
public class Runner {

    public static void main(String args[]) {
        //TODO have a nicer command line interface
        if (args.length < 2) {
            System.out.println("Usage: <commitMngAddress> <tellStoreAddress>");
            System.exit(1);
        }

        // reading parameters
        String commitMng = args[0];
        String tellStr = args[1];

        // single partition
        long chunkCount = 4L;
        // reading 5mb of data
        long chunkSize = 5120000L;

        // client params
        ClientManager clientManager = new ClientManager(commitMng, tellStr);
        ScanMemoryManager scanMemoryManager = new ScanMemoryManager(clientManager, chunkCount, chunkSize);
        Transaction trx = Transaction.startTransaction(clientManager);
        // query params
        String tblName = "testTable";
        ScanQuery query = new ScanQuery(tblName);
//        query.addProjection(fieldPos);
//        ScanQuery.CNFClause clause = query.new CNFClause();
//        clause.addPredicate(ScanQuery.CmpType.GREATER, fieldPos, PredicateType.create(0));
//        query.addSelection(clause);


        // query itself
        ScanIterator scanIt = trx.scan(scanMemoryManager, query);

        Schema schema = new Schema();
        schema.addField(FieldType.INT, "number", true);
        schema.addField(FieldType.TEXT, "text1", true);
        schema.addField(FieldType.BIGINT, "largenumber", true);
        schema.addField(FieldType.TEXT, "text2", true);

        int cnt = 0;
        while (scanIt.next()) {
            // get records
            getRecords(schema, scanIt.address(), scanIt.length());
            cnt++;
        }
        trx.commit();
        System.out.println("Chunks-->" + cnt);
    }

    public static void getRecords(Schema schema, long address, long length) {
        int offset = 0;
        sun.misc.Unsafe unsafe = Unsafe.getUnsafe();
        int cnt = 0;
        while (offset != length) {
            offset += 8;
            for (FieldType fieldType : schema.fixedSizeFields()) {
                switch (fieldType) {
                    case SMALLINT:
                        //TODO not a 16 bit
                        System.out.println(fieldType + "->>" + unsafe.getShort(address + offset));
                        offset += 2;
                        break;
                    case INT:
                    case FLOAT:
                        if (unsafe.getInt(address + offset) > 10)
                            System.out.println(fieldType + "->>" + unsafe.getInt(address + offset));
                        offset += 4;
                        break;
                    case DOUBLE:
                    case BIGINT:
                        long aLong = unsafe.getLong(address + offset);
                        if (9223372032559808513L != aLong)
                            throw new IllegalStateException("Error while reading expected record");
                        offset += 8;
                        break;
                    default:
                        throw new IllegalStateException(String.format("<%s> not valid type!", fieldType));
                }
            }
            try {
                for (FieldType fieldType : schema.variableSizedFields()) {
                    int ln = unsafe.getInt(address + offset);
                    offset += 4;
                    String str = readString(unsafe, address + offset, ln);
                    if (str.length() != 94 && str.length() != 147)
                        throw new IllegalStateException("Error while reading expected record");
                    offset += ln;
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            if (offset %8 != 0)
                offset += 8 - (offset % 8);
            cnt++;
        }
        System.out.println(String.format("RECORDS FOUND: %d", cnt));
    }

    public static String readString(sun.misc.Unsafe u, long add, int length) throws UnsupportedEncodingException {
        byte[] str = new byte[length];
        for (int i = 0; i < length; ++i) {
            str[i] = u.getByte(add + i);
        }
        return new String(str, "UTF-8");
    }
}
