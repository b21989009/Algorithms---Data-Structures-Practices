#ifndef COMPRESSION_H
#define COMPRESSION_H
#include <iostream>
#include <cstdio>
#include <cstring>
#include <cstdlib>
#include "BinaryTree.h"
#include "LinkedList.h"
#include "EncodedCharacter.h"

/* Text compression system based on Huffman algorithm */
class Compression {

private:
    vector<string> allCharactersOfFile; // Character sequence of most recently processed input file.

    LinkedList listOfTreeToBeMerged; // Temporarily holds subtrees till they all merge into one big encodingTree
    BinaryTree encodingTree;

    vector<EncodedCharacter*> encodedCharacters;


public:

    Compression(){
        encodedCharacters = {};
        allCharactersOfFile = {};
    }


    void processTerminalCommand(string& command);

    void extractAllCharactersFromFile(string& inputFile);


    /* Build encodingTree, encode tree characters, encode the text sequence.  */
    void encode();

        void createTreeLeavesAndTheirFrequencies();
        void createEncodingTree();
        void buildEncodingMap(TreeNode* recursiveNode, const string& appendToEncoding="", string previousDigitsOf_TheEncodingOfThisCharacter="");
        void encodeTheTextSequence();   // terminal output, as well as text output at "encoded.txt"


    string printAndGetCharacterEncoding(const string& character);

    void decodeTheTextSequence(); // terminal output


    // Outputting encoding tree to terminal.
    void listTree(){

        if (encodedCharacters.empty()){
            cout<< "Cannot List Tree before Encoding! Encode command has to be performed first.\n";
            return;
        }

        encodingTree.listTheTree();
    };



    ~Compression() {
        if (!(encodedCharacters.empty())){

            for (EncodedCharacter* encodedCharacter : encodedCharacters) {

                delete encodedCharacter;
                encodedCharacter = nullptr;
            }

        }
    };


};


#endif
