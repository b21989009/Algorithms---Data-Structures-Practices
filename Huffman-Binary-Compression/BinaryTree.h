#ifndef BINARYTREE_H
#define BINARYTREE_H
#include "TreeNode.h"


class BinaryTree {

public:

    TreeNode* root;


    BinaryTree(){  root = nullptr; };

    ~BinaryTree() {   deleteTheTree(root);  };


    void listTheTree(){

        listTree(root, "Root", "", "0" );
    };


private:


    /* Example tree listing format:

     for input text:

     go go gophers
     loves  cake

     in string form: "go go gophers\nloves\tcake"



-> Root
     -> R
     |    -> R
     |    |    -> R
     |    |    |    -> R
     |    |    |    |    -> R: "l"
     |    |    |    |    -> L: "r"
     |    |    |    -> L
     |    |    |         -> R: "h"
     |    |    |         -> L: "p"
     |    |    -> L
     |    |         -> R: "\n"
     |    |         -> L: "s"
     |    -> L
     |         -> R: "o"
     |         -> L
     |              -> R: " "
     |              -> L: "k"
     -> L
          -> R
          |    -> R: "e"
          |    -> L: "g"
          -> L
               -> R
               |    -> R: "a"
               |    -> L: "c"
               -> L
                    -> R: "\t"
                    -> L: "v"

     */

    // Outputting the tree to terminal
    void listTree(TreeNode* recursiveNode, const string& title, string callHistoryFor_HorizontalIndentation, const string& appendToCallHistory){

        // Preorder Traversal but in reverse direction: MIDDLE-RIGHT-LEFT (So that Right child will appear above Left child.)

        if(recursiveNode != nullptr) {

            /* From root to leaves : from left of the screen to the right. Therefore, titles has indentation according to their level on tree,
             which is determined by callHistoryFor_HorizontalIndentation.  */
            printIndentationSequence(callHistoryFor_HorizontalIndentation);

            /* appendToCallHistory:  1 for right, 0 for left (recursive) call. As can be seen from the example listing format above,
             there are straight vertical lines between right child and left child of a parent. Which is, after right child and before left child.   */
            callHistoryFor_HorizontalIndentation += appendToCallHistory;

            cout<< "-> " << title;  // titles are "R" (right branch) or "L" (left branch) or "Root".


            // MIDDLE
            if (recursiveNode->isLeaf()) {  // Only leaf nodes hold characters.

                cout<< ": \"";

                recursiveNode->printCharacter();

                cout<<"\"\n";

            }
            else
                cout<<"\n";


            // RIGHT
            listTree(recursiveNode->rightChild, "R", callHistoryFor_HorizontalIndentation, "1");


            // LEFT
            listTree(recursiveNode->leftChild, "L", callHistoryFor_HorizontalIndentation, "0");


        }

    };


    // Nodes go higher levels from root to leaves: from left of the screen to the right. Therefore, nodes has indentation according to their level.
    static void printIndentationSequence (string& callHistoryFor_HorizontalIndentation){

        /* As can be seen from the example listing format above,
             there are straight vertical lines after right child and before left child of a parent.

             Therefore, callHistoryFor_HorizontalIndentation determines where the indentation sequence should have vertical lines.  */

        for (char oneOrZero : callHistoryFor_HorizontalIndentation) {

            int isVerticalLine = oneOrZero - '0';


            if (isVerticalLine)
                cout<< "|    ";

            else
                cout<< "     ";

        }

    };



    void deleteTheTree(TreeNode* recursiveNode){
        // Post Order Traversal

        if (recursiveNode != nullptr) {

            // Delete LEFT subtree
            deleteTheTree(recursiveNode->leftChild);

            // Delete RIGHT subtree
            deleteTheTree(recursiveNode->rightChild);

            // Delete MIDDLE (parent)
            delete recursiveNode;

        }

    };

};


#endif
