grammar cdsdebug;
options {
    output=AST;
    ASTLabelType=CommonTree; // type of $stat.tree ref etc...
}


class MyParser extends Parser;

stmt:  (triple | uribody) NEWLINE
    ;      

triple      
    :   node node node node
    ;    

node	:	uri body
	|	 nameditem
	;
	
uri 	:	 NS COLON LOCALNAME | ANGLEOPEN AOBSOLUTEURI ANGLECLOSE ;

body 	:	 QUOTE bodytext QUOTE ;

nameditem : BRACKETOPEN itemname BRACKETCLOSE ;	

bodytext 
	:	 TEXT ;
	
itemname : TEXT ;

uribody :	 uri body;
	
class MyLexer extends Lexer;

NEWLINE :	 '\n';
NS 	:	 ('a'..'z'|'A'..'Z')+;
COLON 	:	 ':';
LOCALNAME 
	:	 ('a'..'z'|'A'..'Z')+;
ANGLEOPEN 
	:	 '<';
	ANGLECLOSE : '>';
		
