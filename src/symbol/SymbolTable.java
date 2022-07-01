package symbol;

import java.util.Hashtable;

public class SymbolTable {

    private Hashtable table;

    public SymbolTable() {
        table = new Hashtable();
    }

    public void push(String w, Id i) {
        table.put(w, i);
    }

    public Id getLocal(String w) {
        return (Id) this.table.get(w);
    }

}
