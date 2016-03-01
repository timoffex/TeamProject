package team3.codefile;/**   An interface for the ADT stack.   @author Frank M. Carrano   @author Timothy M. Henry   @version 4.0
   UPDATED by C. Lee-Klawender*/public interface StackInterface<T>{   /** Adds a new entry to the top of this stack.       @param newEntry  An object to be added to the stack. */   public boolean push(T newEntry);   /** Removes and returns this stack's top entry.       @return  The object at the top of the stack.                or null if the stack is empty*/   public T pop();   /** Retrieves this stack's top entry (without removing).       @return  The object at the top of the stack.                or null if the stack is empty. */   public T peek();   /** Detects whether this stack is empty.       @return  True if the stack is empty. */   public boolean isEmpty();
   /** Returns number of items in this stack
    @return: Number of items */
   public int size();
} // end StackInterface