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
        long chunkCount = 1L;
        // reading 5mb of data
        long chunkSize = 5120000L;
        // create table testTable (id int);
        // client params
        ClientManager clientManager = new ClientManager(commitMng, tellStr, chunkCount, chunkSize);
        System.out.println("===Client_Created===");
        Transaction trx = Transaction.startTransaction(clientManager);
        System.out.println("===Transaction_Created===");

        // query params
        short fieldPos = 1;
        ScanQuery query = new ScanQuery();
        query.addProjection(fieldPos);
        ScanQuery.CNFCLause clause = query.new CNFCLause();
        clause.addPredicate(ScanQuery.CmpType.GREATER, fieldPos, PredicateType.create(0));
        query.addSelection(clause);

        // serialze?
        query.serialize();
        String tblName = "testTable";
        short[]proj = null;
        System.out.println("===========");

        // query itself
        ScanIterator scanIt = trx.scan(query, tblName, proj);

        while (scanIt.next()) {
            System.out.println("Length->" + scanIt.length());
            System.out.println("Address->" + scanIt.address());
        }

        System.out.println(clientManager.tellLib);
    }
}
