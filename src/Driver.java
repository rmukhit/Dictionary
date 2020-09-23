/** The Driver class for CompactPrefixTree */
public class Driver {
    public static void main(String[] args) {
        Dictionary dict = new CompactPrefixTree();

            dict.add("cat");
            dict.add("cart");
            dict.add("csrts");
            dict.add("car");
            dict.add("doge");
            dict.add("doghouse");
            dict.add("wrist");
            dict.add("wrath");
            dict.add("wristle");

            dict.print();

//        System.out.println(dict.check("flox"));



//            int i = CompactPrefixTree.getIndexOfCharacter('c');
//            System.out.println(i);
            // Add other "tests"
    }
}
