import java.util.LinkedList;

/**
 * Basic implementation with a LinkedList.
 */
public class ListQueue implements URLQueue {

  private final LinkedList<String> queue;

  public ListQueue() {
    this.queue = new LinkedList<String>();
  }

  public boolean isEmpty() {
    return this.queue.size() == 0;
  }

  public boolean isFull() {
    return false;
  }

  public void enqueue(String url) {
    this.queue.add(url);
  }

  public String dequeue() {
    return this.queue.remove();
  }

}