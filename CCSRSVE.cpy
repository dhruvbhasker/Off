      *****************************************************************
      **  MEMBER :  CCSRSCVE                                         **
      **  REMARKS:  RECORD LAYOUT FOR THE CSSCVE OUTPUT FILE         **
      *****************************************************************
      **  DATE     AUTH.  DESCRIPTION                                **
      **                                                             **
SCIEX2**  11DEC08  CTS    CREATION OF MODULE                         **
AQF079**  01FEB09  CTS    DECLARATION OF COMPLETE LAYOUT OF CLIENT   **
AQF079**                  EXTRACT                                    **
SCCR30**  04FEB09  CTS    INCREASE IN FILE LENGTH TO INCREASE THE    **
SCCR30**                  LENGTH OF CUSTOMER SEQUENCE NUMBER FIELD   **
NWLAD2**  29JUL09  CTS    ADDITION OF NEW FIELD FOR BENEFICIARY      **
NWLAD2**                  ANNUITY PERIOD CODE                        **
ATF235**  05DEC09  CTS    CHANGED THE LAYOUT                         **
C12168**  04MAR11  CTS    REMOVED COMBINE-FLG-CD SECOND OCCURANCE    **
M226E1**  31MAY13  CTS    TO INCLUDE NEW FIELD IN SCV FOR PURPOSE OF **
M226E1**                  TAX CERTIFICATE                            **
P14406**  08AUG17  CTS    ADDED CELL NUMBER IN CLIENT EXTRACT        **
S20733**  11NOV21  CTS    BUG FIX FOR RECORD GETTING TRUNCATED       **
      *****************************************************************

AQF079*       01  RSCVE-SEQ-REC-INFO                   PIC X(310).
AQF079       01  RSCVE-SEQ-REC-INFO.
AQF079           05  RSCVE-REC-TYPE                     PIC 9(01).
AQF079           05  RSCVE-DATA-TYP-CD                  PIC 9(01).
AQF079           05  RSCVE-HOST-CLI-ID                  PIC X(10).
SCCR30*AQF079      05  RSCVE-CUST-SEQ-NUM                 PIC 9(01).
SCCR30           05  RSCVE-CUST-SEQ-NUM                 PIC 9(02).
AQF079           05  RSCVE-POL-ID                       PIC 9(07).
AQF079           05  RSCVE-NAME-KANJI                   PIC X(50).
AQF079           05  RSCVE-NAME-KANA                    PIC X(50).
AQF079           05  RSCVE-BIRTH-DT                     PIC X(10).
AQF079           05  RSCVE-GENDER-CD                    PIC 9(01).
AQF079           05  RSCVE-ADDRESS-CD                   PIC X(08).
S20733*          05  RSCVE-ADDR-DTL-KANA                PIC X(60).
S20733           05  RSCVE-ADDR-DTL-KANA                PIC X(72).
AQF079           05  RSCVE-SMOKER-CD                    PIC X(01).
AQF079           05  RSCVE-TEL-NO-CD                    PIC X(50).
AQF079           05  RSCVE-EMAIL-ADDR-CD                PIC X(50).
AQF079           05  RSCVE-BNFY-REL-CD                  PIC X(05).
AQF079           05  RSCVE-BNFY-PCT                     PIC 9(03).
AQF079           05  RSCVE-BNFY-DSGN-CD                 PIC X(01).
ATF235           05  RSCVE-COMBINE-FLG-CD               PIC 9(01).
ATF235*NWLAD2           05  RSCVE-BNFY-ANTY-PERI-CD            PIC X(02).
C12168*AQF079           05  RSCVE-COMBINE-FLG-CD               PIC 9(01).
ATF235           05  RSCVE-BNFY-ANTY-PERI-CD            PIC X(02).
AQF079
M226E1           05  RSCVE-DEATH-DT                     PIC X(10).
P14406           05  RSCVE-CELL-NO-CD                   PIC X(14).
S20733           05  RSCVE-LARGE-PRD-CD                 PIC 9(03).
S20733           05  RSCVE-LARGE-PRD-CD-R               REDEFINES
S20733               RSCVE-LARGE-PRD-CD                 PIC X(03).
S20733           05  RSCVE-SMALL-PRD-CD                 PIC 9(03).
S20733           05  RSCVE-SMALL-PRD-CD-R               REDEFINES
S20733               RSCVE-SMALL-PRD-CD                 PIC X(03).
S20733           05  RSCVE-CLI-TYP-CD                   PIC 9(01).
S20733           05  RSCVE-UPDT-IND                     PIC 9(01).
P14406           05  RSCVE-END-FILLER                   PIC X(01).
      *****************************************************************
      **                 END OF COPYBOOK CCSRSCVE                    **
      *****************************************************************
