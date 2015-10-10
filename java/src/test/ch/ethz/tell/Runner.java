package ch.ethz.tell;

/**
 */
public class Runner {

    public static void main (String args[]) {
        //TODO have a nicer command line interface
        if (args.length < 2) {
            System.out.println("Usage: <commitMngAddress> <tellStoreAddress>");
            System.exit(1);
        }

        String commitMng = args[0];
        String tellStr = args[1];

        // single partition
        long chunkCount = 4L;
        // reading 5mb of data
        long chunkSize = 5120000L;
        // create table testTable (id int);
        // client params
        ClientManager clientManager = new ClientManager(commitMng, tellStr, chunkCount, chunkSize);
        Transaction trx = Transaction.startTransaction(clientManager);
        // query params
        short fieldPos = 0;
        ScanQuery query = new ScanQuery();
//        query.addProjection(fieldPos);
//        ScanQuery.CNFCLause clause = query.new CNFCLause();
//        clause.addPredicate(ScanQuery.CmpType.GREATER, fieldPos, PredicateType.create(0));
//        query.addSelection(clause);

        // serialze?
//        query.serialize();
        String tblName = "testTable";
        short[]proj = null;

        // query itself
        ScanIterator scanIt = trx.scan(query, tblName, proj);

        Schema schema = new Schema();
        schema.addField(Schema.FieldType.INT, "number", true);
        schema.addField(Schema.FieldType.TEXT, "text1", true);
        schema.addField(Schema.FieldType.BIGINT, "largenumber", true);
        schema.addField(Schema.FieldType.TEXT, "text2", true);

        System.out.println("===========");
        int offset = 0;
        sun.misc.Unsafe unsafe = Unsafe.getUnsafe();
        while (scanIt.next()) {
            System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@2");
            System.out.println("Length->" + scanIt.length());
            System.out.println("Address->" + scanIt.address());
            long address = scanIt.address();
            for (Schema.FieldType fieldType : schema.fixedSizeFields()) {
                switch(fieldType){
                    case NOTYPE:
                        break;
                    case NULLTYPE:
                        break;
                    case SMALLINT:
                        break;
                    case INT:
                        offset += 2;
                        System.out.println(fieldType + "->>" + unsafe.getInt(address + offset));
                        offset += 4;
                        break;
                    case BIGINT:
                        offset += 6;
                        System.out.println(fieldType + "->>" + unsafe.getLong(address + offset));
                        offset += 8;
                        break;
                    case FLOAT:
                        break;
                    case DOUBLE:
                        break;
                    case TEXT:
                        break;
                    case BLOB:
                        break;
                }

            }
            for (Schema.FieldType fieldType : schema.variableSizedFields()) {
                System.out.println(fieldType + "->>" + unsafe.getChar(address + offset));
                offset += fieldType.toUnderlying();
            }
        }
        trx.commit();
        System.out.println("-----------------");
//        System.out.println(clientManager.tellLib);
    }
}
