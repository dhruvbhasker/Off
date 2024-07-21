      *****************************************************************
      **  MEMBER : CCSR98F0                                          **
      **  REMARKS: RECORD LAYOUT FOR THE                             **
      **           SUSPENSE REGISTER EXTRACT FILE FOR L-SPWL         **
      *****************************************************************
      **  DATE     AUTH.  DESCRIPTION                                **
M245O1**  23MAY14  CTS    INITIAL VERSION                            **
130784**  18DEC20  CTS    FIX FOR MRF POLICIS                        **
      *****************************************************************
  
       01  R98F0-SEQ-REC-INFO.
           05  R98F0-COMPANY-CODE                PIC X(02).
           05  R98F0-SBSDRY-CO-ID                PIC X(02).
           05  R98F0-CRCY-CD                     PIC X(02).
           05  R98F0-MLJ-ACCT                    PIC X(08).
           05  R98F0-ING-ACCT                    PIC X(06).
           05  R98F0-ELAPSED-DAYS                PIC 9(05).
           05  R98F0-POLICY-NUMBER               PIC X(10).
           05  R98F0-STATUS-CODE                 PIC X(04).
           05  R98F0-PLAN-RS.
               10  R98F0-PLAN-CODE               PIC X(05).
               10  R98F0-RATE-SCALE              PIC X(01).
           05  R98F0-SUSPENSE-AMOUNT             PIC S9(11)V9(02).
           05  R98F0-SUSPENSE-DATE               PIC X(10).
           05  R98F0-SEG-FUND-CD                 PIC X(01).
           05  R98F0-PREM-WAV-CD                 PIC X(01).
           05  R98F0-POL-BUS-CLAS-CD             PIC X(01).
           05  R98F0-PREV-UPDT-DT                PIC X(10).
           05  R98F0-PAYO-CRCY-CD                PIC X(02).
           05  R98F0-PAYO-CRCY-RT                PIC S9(9)V9(9).
           05  R98F0-POL-CRCY-AMT                PIC S9(11)V9(02).
130784     05  R98F0-REC-ORDER-CD                PIC X(01).
130784         88 R98F0-REC-ORDER-NOT-MRF        VALUE '1'.
130784         88 R98F0-REC-ORDER-MRF            VALUE '2'.
      *****************************************************************
      **                 END OF COPYBOOK CCSR98F0                    **
      *****************************************************************
