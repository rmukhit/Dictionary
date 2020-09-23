import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/** CompactPrefixTree class, implements Dictionary ADT and
 *  several additional methods. Can be used as a spell checker.
 *  Fill in code in the methods of this class. You may add additional methods. */
public class CompactPrefixTree implements Dictionary {

    private Node root; // the root of the tree

    /** Default constructor.
     * Creates an empty "dictionary" (compact prefix tree).
     * */
    public CompactPrefixTree(){

        root = new Node();
    }

    /**
     * Creates a dictionary ("compact prefix tree")
     * using words from the given file.
     * @param filename the name of the file with words
     */
    public CompactPrefixTree(String filename) {

        root = new Node();

        //read each line from a file and add it to dict
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            String line;
            while ((line = br.readLine()) != null) {
                add(line);
            }

            br.close();

        } catch (Exception e) {

        }

    }

    /** Adds a given word to the dictionary.
     * @param word the word to add to the dictionary
     */
    public void add(String word) {
        root = add(word.toLowerCase(), root); // Calling private add method
    }

    /**
     * Checks if a given word is in the dictionary
     * @param word the word to check
     * @return true if the word is in the dictionary, false otherwise
     */
    public boolean check(String word) {
        return check(word.toLowerCase(), root); // Calling private check method
    }

    /**
     * Checks if a given prefix is stored in the dictionary
     * @param prefix The prefix of a word
     * @return true if the prefix is in the dictionary, false otherwise
     */
    public boolean checkPrefix(String prefix) {
        return checkPrefix(prefix.toLowerCase(), root); // Calling private checkPrefix method
    }


    /**
     * Prints all the words in the dictionary, in alphabetical order,
     * one word per line.
     */
    public void print() {
        print("", root); // Calling private print method
    }

    /**
     * Print out the nodes of the compact prefix tree, in a pre-order fashion.
     * First, print out the root at the current indentation level
     * (followed by * if the node's valid bit is set to true),
     * then print out the children of the node at a higher indentation level.
     */
    public void printTree() {

        printTree(root, 0);
    }

    /**
     * Print out the nodes of the tree to a file, using indentations to specify the level
     * of the node.
     * @param filename the name of the file where to output the tree
     */
    public void printTree(String filename) {
        // I used printwriter to write tree to a file
        try (PrintWriter pw = new PrintWriter(filename)){
            // call private helper method with printwriter pw as a parameter
           printTree(pw, root, 0);
        } catch (IOException e) {
            System.out.println("Error writing to a file: "  + e);
        }


    }

    /**
     * Return an array of the entries in the dictionary that are as close as possible to
     * the parameter word.  If the word passed in is in the dictionary, then
     * return an array of length 1 that contains only that word.  If the word is
     * not in the dictionary, then return an array of numSuggestions different words
     * that are in the dictionary, that are as close as possible to the target word.
     * Implementation details are up to you, but you are required to make it efficient
     * and make good use ot the compact prefix tree.
     *
     * @param word The word to check
     * @param numSuggestions The length of the array to return.  Note that if the word is
     * in the dictionary, this parameter will be ignored, and the array will contain a
     * single world.
     * @return An array of the closest entries in the dictionary to the target word
     */
    public String[] suggest(String word, int numSuggestions) {

        return suggest(word, numSuggestions, root);
    }

    // ---------- Private helper methods ---------------

    /**
     *  A private add method that adds a given string to the tree
     * @param s the string to add
     * @param node the root of a tree where we want to add a new string

     * @return a reference to the root of the tree that contains s
     */
    private Node add(String s, Node node) {

        Node value;

        // base case when we will recursively add words,
        // and if substring of String s will be "", then return this node
        if (s.length() == 0){
            value = node;
        //base case when tree is empty
        } else if (node.children[getIndexOfCharacter(s.charAt(0))] == null) {
            Node newWord = new Node();
            newWord.prefix = node.prefix + s;
            newWord.isWord = true;
            node.children[getIndexOfCharacter(s.charAt(0))] = newWord;
            value = node;
        } else {
            // we should be exactly in the node that has prefix starting from s.charAt(0)
            String temp = node.prefix+s;
            int index = getIndexOfCharacter(s.charAt(0));
            Node baseNode = node.children[index];

            // as in project description, we search for common prefix
            String commonP = findLongestCommonPrefix(baseNode.prefix, node.prefix+s);

            if (commonP.equals(baseNode.prefix)) {
                String suffix = temp.substring(commonP.length());
                value = add(suffix, baseNode);
                // make newBaseNode as child of node
                node.children[getIndexOfCharacter(s.charAt(0))] = value;
                value = node;
                return value;
            }
            // create new Node called newBaseNode
            Node newBaseNode = new Node();
            // Oldsuffix = baseNode.prefix - commonP (pseudocode)
            // search for suffix of existed prefix in baseNode
            String oldSuffix = baseNode.prefix.substring(commonP.length(), baseNode.prefix.length());


            if(oldSuffix.length() > 0) {
//                baseNode.prefix = oldSuffix;
                newBaseNode.prefix = commonP;
                // make baseNode the child of newBaseNode
                newBaseNode.children[getIndexOfCharacter(oldSuffix.charAt(0))] = baseNode;
            } else {
                newBaseNode = baseNode;
            }

            // newSuffix = String s - commonP (pseudocode)
            String newSuffix = temp.substring(commonP.length(), temp.length());
            // if newSuffix is "", then set newBaseNode as word
            if (newSuffix.length()== 0){
                newBaseNode.isWord = true;
            }

            //recursively add newSuffix to newBaseNode
            value = add(newSuffix, newBaseNode);
            // make newBaseNode as child of node
            node.children[getIndexOfCharacter(s.charAt(0))] = value;
            value = node;
        }

        return value;
    }

    /**
     *  A private printTree method that helps to print structure of the tree
     * @param node the Node
     * @param numIndentations the number of indentations
     *
     */
    private void printTree(Node node, int numIndentations) {
        // deal with indentations
        String s = "";
        for (int i = 0; i <= numIndentations; i++) {
            s += " ";
        }
        // print the beginning of the tree
        if (node.prefix.equals("")) {
            System.out.println(node.prefix);
        }
        //recursively search for words and prefixes
        if (node != null) {
            for(Node c : node.children) {
                if (c != null) {
                    if (c.isWord) {
                        System.out.println(s + c.prefix + "*");
                    } else {
                        System.out.println(s + c.prefix);
                    }
                    printTree(c, numIndentations + 1);
                }
            }
        }
    }

    /**
     *  A private printTree method that helps to write structure of the tree to a file
     * @param pw Printwriter where we add all the words and prefixes we want to write to a file
     * @param node the Node
     * @param numIndentations the number of indentations
     *
     */
    private void printTree(PrintWriter pw, Node node, int numIndentations) {
        // deal with indentations
        String s = "";
        for (int i = 0; i <= numIndentations; i++) {
            s += " ";
        }
        // print the beginning of the tree
        if (node.prefix.equals("")) {
            pw.println(node.prefix);
        }

        //recursively search for words and prefixes
        if (node != null) {
            for(Node c : node.children) {
                if (c != null) {
                    if (c.isWord) {
                        pw.println(s + c.prefix + "*");
                    } else {
                        pw.println(s + c.prefix);
                    }
                    printTree(pw, c,numIndentations + 1);
                }
            }
        }


    }

    /**
     *  A private helper method that return the index of character
     * @param c char

     * @return index of this char c
     */
    private int getIndexOfCharacter(char c) {
        int index = (int)c - (int)'a';
        return index;
    }

    /**
     *  A private helper method that finds the longest common prefix of 2 srings
     * @param s1 String
     * @param s2 String
     * @return longest common prefix
     */
    private String findLongestCommonPrefix(String s1, String s2) {
        int minLength = Math.min(s1.length(), s2.length());
        for (int i = 0; i < minLength; i++) {
            if (s1.charAt(i) != s2.charAt(i)) {
                return s1.substring(0, i);
            }
        }
        return s1.substring(0, minLength);
    }


    /** A private method to check whether a given string is stored in the tree.
     *
     * @param s the string to check
     * @param node the root of a tree
     * @return true if the prefix is in the dictionary, false otherwise
     */
    private boolean check(String s, Node node) {
        boolean result = false;
        Node baseNode;
        // make sure that length of s > 0 and node has child that starts with s.charAt(0)
        if (s.length()>0 && node.children[getIndexOfCharacter(s.charAt(0))]!= null){
            // use node that starts with s.charAt(0), call it baseNode
            baseNode = node.children[getIndexOfCharacter(s.charAt(0))];
            // if s starts with prefix of baseNode
            if (s.indexOf(baseNode.prefix) == 0) {
                // if s and prefix of baseNode equal and baseNode is word, then we found a word
                if (baseNode.prefix.equals(s) && baseNode.isWord) {
                    result = true;
                // else recursively check substring of s in baseNode
                } else {
                    s = s.substring(baseNode.prefix.length(), s.length());
                    result = check(s, baseNode);
                }
            }
        }
        return result;
    }


    /**
     * A private recursive method to check whether a given prefix is in the tree
     *
     * @param prefix the prefix
     * @param node the root of the tree
     * @return true if the prefix is in the dictionary, false otherwise
     */
    private boolean checkPrefix(String prefix, Node node) {
        // recursively search for prefixes
        boolean result = false;
        if (node != null) {
            for(Node c : node.children) {
                if (c != null && !result) {
                    String commonP = findLongestCommonPrefix(c.prefix, prefix);
                    if (commonP.equals(prefix) || commonP.equals(c.prefix)) {
                        if (commonP.equals(prefix)) {
                            result = true;
                            break;
                        } else {
                            prefix = prefix.substring(commonP.length(), prefix.length());
                            result = checkPrefix(prefix, c);
                        }
                    }

                }
            }
        }
        return result;
    }



    /**
     * Outputs all the words stored in the dictionary
     * to the console, in alphabetical order, one word per line.
     * @param s the string obtained by concatenating prefixes on the way to this node
     * @param node the root of the tree
     */
    private void print(String s, Node node) {
        // print first root node
        if (node.prefix.equals("")) {
            System.out.println(node.prefix);
        }
        // recursively search for words
        if (node != null) {
            for(Node c : node.children) {
                if (c != null) {
                    if (c.isWord) {
                        System.out.println(s + c.prefix);
                    }
                    print(s + c.prefix, c);
                }
            }
        }
    }

    /**
     * private helper method that returns the array of suggested words
     * @param word the word for which we are searching suggestions
     * @param numSuggestions number of suggestions
     * @param node the root of the tree
     *
     * @return the array with suggested words
     */
    private String[] suggest(String word, int numSuggestions, Node node) {
        String[] result;
        //base case
        if (check(word, node)){
            result = new String[1];
            result[0] = word;
        } else {
            result = new String[numSuggestions];
            // call helper method getClosestWords
            List<String> closeSuggestions = getClosestWords(word,"", numSuggestions, node, true);

            // if there is not enough suggestions, then divide word by half
            // and search for suggestions for this half word
            if (closeSuggestions.size() < numSuggestions) {
                String newWord = word.substring(0,word.length()/2);
                List<String> similarSuggestions = getClosestWords(newWord,"",
                        numSuggestions-closeSuggestions.size(), node, true);
                // add them to our suggestions
                closeSuggestions.addAll(similarSuggestions);
            }

            result = closeSuggestions.toArray(result);

        }
        return result;
    }
    /**
     * 2nd private helper method that returns the array of suggested words
     * @param word the word for which we are searching suggestions
     * @param wordPrefix the string obtained by concatenating prefixes on the way to this node
     * @param numSuggestions number of suggestions
     * @param node the root of the tree
     * @param firstCall boolean that indicates whether we should look for suggestions
     *
     * @return the list of suggested words
     */
    private List<String> getClosestWords(String word, String wordPrefix, int numSuggestions, Node node, boolean firstCall) {
        List<String> result = new ArrayList<>();
        int resultCount = numSuggestions;
        Node baseNode = node;
        if (firstCall && word.length()>0 && node.children[getIndexOfCharacter(word.charAt(0))]!=null){
            baseNode = node.children[getIndexOfCharacter(word.charAt(0))];
        }

        if (baseNode == null){
            baseNode = node;
        }

        if (numSuggestions > 0){
            String commonP = findLongestCommonPrefix(baseNode.prefix, word);
            if (commonP.equals(baseNode.prefix)) {
                result = getClosestWords(word.substring(commonP.length(),word.length()),
                        wordPrefix + word.substring(0,commonP.length()), numSuggestions, baseNode, true);
            } else {
                if ((commonP.length() > 0 && firstCall) || !firstCall){
                    // in order to preserve the prefix of the word
                    wordPrefix = wordPrefix + baseNode.prefix;
                }
                // if suggested word isWord, then we add this word to the list
                // (only here we add words to the list of suggestions)
                if (baseNode.isWord) {
                    result.add(wordPrefix);
                    numSuggestions--;
                }
                // recursively add children of baseNode
                for(Node c : baseNode.children) {
                    if (numSuggestions > 0){
                        if (c != null) {
                            List<String> words = getClosestWords(word, wordPrefix, numSuggestions, c, false);
                            numSuggestions = numSuggestions - words.size();
                            result.addAll(words);
                        }

                    }
                }
            }
        }
        // cut list to the exact numbers of suggestions that we needed
        if (result.size() > resultCount){
            result = result.subList(0, resultCount);
        }
        return result;
    }


    // --------- Private class Node ------------
    // Represents a node in a compact prefix tree
    private class Node {
        String prefix; // prefix stored in the node
        Node children[]; // array of children (26 children)
        boolean isWord; // true if by concatenating all prefixes on the path from the root to this node, we get a valid word

        Node() {
            isWord = false;
            prefix = "";
            children = new Node[26]; // initialize the array of children
        }
    }

}
