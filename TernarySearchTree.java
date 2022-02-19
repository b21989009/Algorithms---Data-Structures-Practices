
/***

 Mehmet Giray Nacakci / BBM204 Spring2021

 */

import java.util.ArrayList;
import java.util.List;

// Ternary Search Tree
public class TST<Value> {

    public Node<Value> root;

    ArrayList<Value> verticesWithRequestedPrefix; // results of prefix search


    public static class Node<Value> {

        public char c; // determines position of this Node on the tree.

        // Ternary Search Property:
        public Node<Value> left;   // left has its char c alphabetically closer to letter "a" than current Node.
        public Node<Value> right;  // right has its char c alphabetically farther from letter "a" than current Node.
        public Node<Value> mid;    // char c of mid is at least the second letter of a key (a location's normalised name).

        public Value val; // the Vertex (location)
    }

    TST(){
        root = null;
        verticesWithRequestedPrefix = new ArrayList<>();
    }


    // Inserts the key value pair into Ternary Search Tree
    public void put(String key, Value val) {
        // key = location name normalized. Determines the position of a Node in tree.
        // val = Vertex to insert into a Node

        if (key==null || key.length()<1)
            return;

        if (root == null){
            root = new Node<>();
            root.c = key.charAt(0);
        }

        recursiveInsert(key, val, root, 0);

    }


    void recursiveInsert(String key, Value vertex, Node<Value> subtreeRoot, int n_th_letter){

        // LEFT
        if (key.charAt(n_th_letter) < subtreeRoot.c){
            if (subtreeRoot.left == null){
                subtreeRoot.left = new Node<>();
                subtreeRoot.left.c = key.charAt(n_th_letter);
            }
            recursiveInsert(key, vertex, subtreeRoot.left, n_th_letter);
        }

        // MID
        else if (key.charAt(n_th_letter) == subtreeRoot.c){

            // Are we at the right place to insert the vertex?
            if (n_th_letter == key.length()-1){
                // INSERTING vertex
                subtreeRoot.val = vertex; // overwrites if same key already exists.
                return;
            }

            if (subtreeRoot.mid == null){
                subtreeRoot.mid = new Node<>();
                subtreeRoot.mid.c = key.charAt(n_th_letter + 1);

            }
            // one more letter is matched
            recursiveInsert(key, vertex, subtreeRoot.mid, n_th_letter + 1);
        }

        // RIGHT
        else if (key.charAt(n_th_letter) > subtreeRoot.c){
            if (subtreeRoot.right == null){
                subtreeRoot.right = new Node<>();
                subtreeRoot.right.c = key.charAt(n_th_letter);
            }
            recursiveInsert(key, vertex, subtreeRoot.right, n_th_letter);
        }


    }



    // Serving location suggestions, given an at least two-letter prefix.
    public List<Value> valuesWithPrefix(String prefix) {

        verticesWithRequestedPrefix.clear();

        // prefix must be minimum 2 letters
        if (prefix.length() < 2 || root==null)
            return new ArrayList<>();

        recursiveSearch(prefix, root, 0);

        return verticesWithRequestedPrefix;
    }


    // Find the subtree whose all keys start with the desired prefix
    void recursiveSearch( String prefix, Node<Value> subtreeRoot, int n_th_letter ){

        // Prefix is reached
        if (n_th_letter == prefix.length()){
            // all children starts with this prefix, go find them.
            recursiveTraverse(subtreeRoot);
        }


        // LEFT
        else if (prefix.charAt(n_th_letter) < subtreeRoot.c){
            if (subtreeRoot.left != null){
                recursiveSearch(prefix, subtreeRoot.left, n_th_letter);
            }
        }

        // MID
        else if (prefix.charAt(n_th_letter) == subtreeRoot.c){

            // checking if exists a node with exact key as prefix
            if (n_th_letter == prefix.length()-1){
                if(subtreeRoot.val != null)
                    verticesWithRequestedPrefix.add(subtreeRoot.val);
            }

            if (subtreeRoot.mid != null){
                // one more letter is matched
                recursiveSearch(prefix, subtreeRoot.mid, n_th_letter + 1);
            }
        }

        // RIGHT
        else if (prefix.charAt(n_th_letter) > subtreeRoot.c){
            if (subtreeRoot.right != null){
                recursiveSearch(prefix, subtreeRoot.right, n_th_letter);
            }
        }

    }


    // We reached the subtree whose all keys start with the desired prefix. Add all Vertex val-s to the list.
    void recursiveTraverse(Node<Value> subtreeRoot){
        // Traverse itself and all children

        if (subtreeRoot==null)
            return;

        if(subtreeRoot.val != null)
            verticesWithRequestedPrefix.add(subtreeRoot.val);

        if (subtreeRoot.left != null)
            recursiveTraverse(subtreeRoot.left);

        if (subtreeRoot.mid != null)
            recursiveTraverse(subtreeRoot.mid);

        if (subtreeRoot.right != null)
            recursiveTraverse(subtreeRoot.right);

    }


}
