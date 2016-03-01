package team3.codefile;


//------------------------------------------------------
public class GraphTester
{
	   // -------  main --------------
	   public static void main(String[] args)
	   {
	      // build graph
	      Graph<String> myGraph1 = new Graph<String>();
	      myGraph1.addEdge("A", "B", 0);   myGraph1.addEdge("A", "C", 0);  myGraph1.addEdge("A", "D", 0);
	      myGraph1.addEdge("B", "E", 0);   myGraph1.addEdge("B", "F", 0);
	      myGraph1.addEdge("C", "G", 0);
	      myGraph1.addEdge("D", "H", 0);   myGraph1.addEdge("D", "I", 0);
	      myGraph1.addEdge("F", "J", 0);
	      myGraph1.addEdge("G", "K", 0);   myGraph1.addEdge("G", "L", 0);
	      myGraph1.addEdge("H", "M", 0);   myGraph1.addEdge("H", "N", 0);
	      myGraph1.addEdge("I", "N", 0);

	      myGraph1.showAdjTable();



	   }

}
