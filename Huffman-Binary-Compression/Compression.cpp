#include "Compression.h"


void Compression::processTerminalCommand(string& command){

    /* Extracting arguments from command */

    // Some type conversions
    char commandString[command.length()+1];
    strcpy(commandString, command.c_str());

    // Take arguments from the command
    vector<string> arguments{};
    for ( char* argument = strtok(commandString, " ") ; argument != nullptr ; argument = strtok(nullptr, " "))
        arguments.emplace_back(argument);


    /*** Executing the command  */


    int commandIs_Encode_Command = (arguments.size()==3) && (arguments.at(0) == "-i")  &&  (arguments.at(2) == "-encode");
    if(commandIs_Encode_Command){

        // Clear the previous encoding, if there was any.
        if (!(encodedCharacters.empty()))
            encodedCharacters.clear();

        extractAllCharactersFromFile(arguments.at(1));

        encode();

        allCharactersOfFile.clear(); // Clear for upcoming file reads.

    }


    int commandIs_GetSingleCharacterEncoding = (arguments.size()==2) && (arguments.at(0) == "-s") ;
    if(commandIs_GetSingleCharacterEncoding) {
        printAndGetCharacterEncoding(arguments.at(1));
        cout<<"\n";
    }

    int commandIs_Decode_Command =  (arguments.size()==3) && (arguments.at(0) == "-i")  &&  (arguments.at(2) == "-decode");
    if(commandIs_Decode_Command){

        extractAllCharactersFromFile(arguments.at(1));

        decodeTheTextSequence();

        allCharactersOfFile.clear();  // Clear for upcoming file reads.
    }


    int commandIs_ListTree_Command =  (arguments.size()==1) && (command == "-l") ;
    if (commandIs_ListTree_Command)
        listTree();


    int commandNotFound = !(commandIs_Encode_Command || commandIs_Decode_Command || commandIs_GetSingleCharacterEncoding || commandIs_ListTree_Command);
    if (commandNotFound)
        cout<< "Command Not Found!\n";


}


/* Build encodingTree, encode tree characters, encode the text sequence. */
void Compression::encode(){

    createTreeLeavesAndTheirFrequencies();

    createEncodingTree();

    buildEncodingMap(encodingTree.root);

    encodeTheTextSequence();

}


void Compression::extractAllCharactersFromFile(string& inputFile){

    ifstream inputStream;
    inputStream.open(inputFile);

    string allCharacters;
    // Get first line
    getline(inputStream, allCharacters);

    while (inputStream) {

        string nextLine;
        getline(inputStream, nextLine);
        // Since getline discards "\n", it is added back.
        allCharacters += "\n" + nextLine;

    }


    /*  Parsing allCharacters into individual characters, storing them in vector "allCharactersOfFile", lowercase. */
    for (char character : allCharacters){

        char lowercaseChar = (char)(tolower(character));

        allCharactersOfFile.push_back( {lowercaseChar} );
    }

    inputStream.close();

}


void Compression::createTreeLeavesAndTheirFrequencies(){

    for (string characterToSearch : allCharactersOfFile){
        ListNode* listNodeWhichIsBeingSearched = listOfTreeToBeMerged.searchNodeWithCharacter(characterToSearch);

        int isThisANewCharacter = (listNodeWhichIsBeingSearched == nullptr);

        if (isThisANewCharacter) {
            auto* newTreeLeaf = new TreeNode(characterToSearch);
            listOfTreeToBeMerged.insertNodeToRear(newTreeLeaf);
        }

        else
            listNodeWhichIsBeingSearched->treeNodeOnList->characterFrequency_OrTotalFrequencyOfChildren ++;

    }

}


// Encoding tree is created from bottom to top. From leaf towards root, subtrees are created and merged.
void Compression::createEncodingTree(){

    while ( listOfTreeToBeMerged.listSize > 1 ) {

        listOfTreeToBeMerged.searchFor_AndMerge_TheTwoNodes_WhichHas_LeastFrequencies();
    }

    // To create the big main tree, only the root is needed.
    encodingTree.root = listOfTreeToBeMerged.getTheRoot_ForTheNewEncodingTree_thatWillBeCreated();

    // Emptying the temporary list as a preparation for brand new encodings during runtime, in case there might be any.
    listOfTreeToBeMerged.removeFirstNode();

}


/* Conceptually, mapping characters to their encodings.
   In practice, filling the vector "encodedCharacters" with "EncodedCharacter" objects */
void Compression::buildEncodingMap(TreeNode* recursiveNode, const string& appendToEncoding, string previousDigitsOf_TheEncodingOfThisCharacter){

    if (recursiveNode == nullptr)
        return;

    previousDigitsOf_TheEncodingOfThisCharacter += appendToEncoding;


    /*  In Order Traversal  */

    // LEFT
    buildEncodingMap(recursiveNode->leftChild, "0", previousDigitsOf_TheEncodingOfThisCharacter);

    // MIDDLE
    if(recursiveNode->isLeaf())  // Only leaf nodes are holding characters.
        encodedCharacters.push_back( new EncodedCharacter(recursiveNode->getCharacter(), previousDigitsOf_TheEncodingOfThisCharacter) );

    // RIGHT
    buildEncodingMap(recursiveNode->rightChild, "1", previousDigitsOf_TheEncodingOfThisCharacter);

}



/* Outputs to terminal, as well as to "encoded.txt" for easier testing.
      "encoded.txt" will be used as input argument for decoding.     */
void Compression::encodeTheTextSequence(){

    ofstream encodedOutputStream;
    encodedOutputStream.open("encoded.txt");

    for (string& character : allCharactersOfFile) {
        encodedOutputStream<< printAndGetCharacterEncoding( character );
    }

    cout <<"\n";
    encodedOutputStream.close();
}



/***
 *  Note: This function cannot be called from terminal by  "-s character" command (only) for special characters
    such as space, newline, tab ; since the terminal processes them as regular characters.
        For example: entering backslash and n to terminal, it will be processed as two separate characters. '\' , 'n'
 */
string Compression::printAndGetCharacterEncoding(const string& character){

    if ( ! (encodedCharacters.empty()) ) {

        // Search for character
        for (EncodedCharacter* encodedCharacter : encodedCharacters) {
            if (encodedCharacter->getCharacter() == character) {

                string binaryEncoding = encodedCharacter->getBinaryEncoding();
                cout << binaryEncoding;
                return binaryEncoding;
            }

        }

        cout<< "Character Not Found!";
        return "";

    }

    cout << "Encoding must be performed first! This command cannot be performed without encoding first.\n";
    return "";

}



void Compression::decodeTheTextSequence(){

    if (encodedCharacters.empty()){
        cout<< "Cannot Decode before Encode! Encode command has to be performed first!\n";
        return;
    }

    TreeNode* traverseNode = encodingTree.root;

    for (string& inputDigit : allCharactersOfFile) {

        if (inputDigit == "0")
            traverseNode = traverseNode->leftChild;

        else if (inputDigit == "1")
            traverseNode = traverseNode->rightChild;



        if (traverseNode->isLeaf()) { // Only leaf nodes are holding characters

            cout<< traverseNode->getCharacter();

            traverseNode = encodingTree.root;  // Back to root, for the next character.
        }

    }


}

