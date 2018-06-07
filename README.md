**POC Findings**
--
    SBE Performance: TBD
    
    SBE IR Schema
        1. Missing some fields
            Example: Model enum was missing values
    
    SBE JsonPrinter 
        1. Very Fast, almost 5 times faster than Circe 
            (although Circe first decods to types and then encodes to Json)        
   
        2. Does not print a valid JSON
            Example: 
                WRONG: "fuel": Petrol
                CORRECT: "fuel": "Petrol"
                
        2. Does not print all fields 
        (Observed omitted fields are enums and booleans)
            Example:
                OMITTED FIELDS:
                    availabe (bool)
                    code (enum)
                    boosterEnabled (bool) 
                    boostType (enum)
                    discountedModel (enum constant)
                    extras (enum)
                    
        3. Rounds up numbers
            Example:
                REAL VALUE: "mpg" : 35.900001525878906
                ROUNDED VALUE: "mpg" : 35.9
            Example 2:
                REAL VALUE: "seconds" : 12.199999809265137
                ROUNDED VALUE: "seconds" : 12.2 
            
