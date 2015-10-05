package ch.ethz.tell;

/**
 */
public class TestRunner {

    public static void main (String args[]) {
        String commitMng = "";
        String tellStr = "";
        long chunkCount = 1L;
        long chunkSize = 10l;
        // create table testTable (id int);
        // client params
        ClientManager clientManager = new ClientManager(commitMng, tellStr, chunkCount, chunkSize);
        Transaction trx = Transaction.startTransaction(clientManager);

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

        // query itself
        ScanIterator scanIt = trx.scan(query, tblName, proj);
        while (scanIt.next()) {
            System.out.println("Length->" + scanIt.length());
            System.out.println("Address->" + scanIt.address());
        }

        System.out.println(clientManager.tellLib);
    }
}
