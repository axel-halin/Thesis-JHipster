The different standalones must be launched from FML-brute folder or any folder following its structure:

  - Presence of JHipster-Registry folder
  - Presence of jhipsters folder with UAA (generated with JHipster) in it (/uaa)
  - models folder (store dimacs, even if you're not intersted in it the JAR will look for it)
  - phantomjs executable
  - JDL files
  - oracle-jar with ojdbc in it
  
Standalone usages:
  
   - GenerateAll.jar: Generate all JHipster variants including Oracle and H2 related ones. Discard Client/Server standalones.
   - OracleGoogle.jar: Runs the analysis workflow and sends the results to a Google Sheet.
   
  WARNING: Deprecated...
   - GenerateJHipsterVariants.jar: Generate JHipster variants included in the preselection (no Oracle/H2/Client/Server).
   - GenerateRemainder.jar: Generate JHipster variants excluded by the preselection.
   - GenerateWithSearchEngine.jar: Fix to GenerateJHipsterVariants.jar which didn't correctly parse the value of SearchEngine.
   - Oracle.jar: Runs the analysis workflow and store the results in a local csv file (jhipster.csv).
