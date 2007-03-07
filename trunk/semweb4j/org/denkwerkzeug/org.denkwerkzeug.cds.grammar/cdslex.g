grammar cdslex;

stmt    	 :	EXCL subject NEWLINE predicate_object* NEWLINE?;

subject 	 :	text;
predicate 	 :	text;
object 	 	 :	text;

predicate_object :	STAR predicate COLON object NEWLINE
		 |      STAR object NEWLINE
        	 ;
text    	 :	(ALPHA|SPACE|NEWLINE)+;

ALPHA   	 :	('a'..'z'|'A'..'Z')+;
EXCL 		 :	 '!';
STAR    	 :	 '*';
SPACE	 	 :	 ' ';
LBRACE		 :	 '[';
RBRACE		 :	 ']';
COLON	 	 :	 ':';
NEWLINE		 :	 '\n';


