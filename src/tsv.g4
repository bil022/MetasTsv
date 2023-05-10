grammar tsv;
tsvFile
   : (
    renlab
    |
    igm
    )
    EOF
   ;

renlab
    : 'Item' TAB 'Description' (TAB)* EOL
      hdr*
      sid
      row*
    ;

igm
    : row*
    sname
    row* EOF
    ;

hdr
   : field TAB field (TAB)* EOL
   ;

sname
    : 'Sample Name' (TAB field)* EOL
    ;

sid
   : 'sample ID' (TAB field)* EOL
   ;

row
   : field (TAB field)* EOL
   ;

field
   : txt=TEXT
   |
   ;


TAB
   : '\t'
   ;


EOL
   : [\n\r] +
   ;


TEXT
   : ~ [,\n\r\t"]+
   ;
