package prorunvis;

public class KeYobligation {
    // will be replaced by gradle: @PROJECT_DIRECTORY@
    public static final String TRACED_SOURCE_FOLDER
        = "@PROJECT_DIRECTORY@/../DualPivot/src/main/java/";

    public static final String PROLOG = """
\\profile "Java Profile";

\\settings // Proof-Settings-Config-File
{ 
    "Choice" : { 
        "JavaCard" : "JavaCard:off",
        "Strings" : "Strings:on",
        "assertions" : "assertions:safe",
        "bigint" : "bigint:on",
        "floatRules" : "floatRules:strictfpOnly",
        "initialisation" : "initialisation:disableStaticInitialisation",
        "intRules" : "intRules:arithmeticSemanticsIgnoringOF",
        "integerSimplificationRules" : "integerSimplificationRules:full",
        "javaLoopTreatment" : "javaLoopTreatment:efficient",
        "mergeGenerateIsWeakeningGoal" : "mergeGenerateIsWeakeningGoal:off",
        "methodExpansion" : "methodExpansion:modularOnly",
        "modelFields" : "modelFields:treatAsAxiom",
        "moreSeqRules" : "moreSeqRules:off",
        "permissions" : "permissions:off",
        "programRules" : "programRules:Java",
        "reach" : "reach:on",
        "runtimeExceptions" : "runtimeExceptions:ban",
        "sequences" : "sequences:on",
        "wdChecks" : "wdChecks:off",
        "wdOperator" : "wdOperator:L"
     },
    "Labels" : { 
        "UseOriginLabels" : true
     },
    "NewSMT" : { 
        
     },
    "SMTSettings" : { 
        "SelectedTaclets" : [ 
            
         ],
        "UseBuiltUniqueness" : false,
        "explicitTypeHierarchy" : false,
        "instantiateHierarchyAssumptions" : true,
        "integersMaximum" : 2147483645,
        "integersMinimum" : -2147483645,
        "invariantForall" : false,
        "maxGenericSorts" : 2,
        "useConstantsForBigOrSmallIntegers" : true,
        "useUninterpretedMultiplication" : true
     },
    "Strategy" : { 
        "ActiveStrategy" : "JavaCardDLStrategy",
        "MaximumNumberOfAutomaticApplications" : 10000,
        "Timeout" : -1,
        "options" : { 
            "AUTO_INDUCTION_OPTIONS_KEY" : "AUTO_INDUCTION_OFF",
            "BLOCK_OPTIONS_KEY" : "BLOCK_EXPAND",
            "CLASS_AXIOM_OPTIONS_KEY" : "CLASS_AXIOM_FREE",
            "DEP_OPTIONS_KEY" : "DEP_ON",
            "INF_FLOW_CHECK_PROPERTY" : "INF_FLOW_CHECK_FALSE",
            "LOOP_OPTIONS_KEY" : "LOOP_EXPAND",
            "METHOD_OPTIONS_KEY" : "METHOD_EXPAND",
            "MPS_OPTIONS_KEY" : "MPS_MERGE",
            "NON_LIN_ARITH_OPTIONS_KEY" : "NON_LIN_ARITH_NONE",
            "OSS_OPTIONS_KEY" : "OSS_ON",
            "QUANTIFIERS_OPTIONS_KEY" : "QUANTIFIERS_NON_SPLITTING_WITH_PROGS",
            "QUERYAXIOM_OPTIONS_KEY" : "QUERYAXIOM_ON",
            "QUERY_NEW_OPTIONS_KEY" : "QUERY_OFF",
            "SPLITTING_OPTIONS_KEY" : "SPLITTING_DELAYED",
            "STOPMODE_OPTIONS_KEY" : "STOPMODE_DEFAULT",
            "SYMBOLIC_EXECUTION_ALIAS_CHECK_OPTIONS_KEY" : "SYMBOLIC_EXECUTION_ALIAS_CHECK_NEVER",
            "SYMBOLIC_EXECUTION_NON_EXECUTION_BRANCH_HIDING_OPTIONS_KEY" : "SYMBOLIC_EXECUTION_NON_EXECUTION_BRANCH_HIDING_OFF",
            "USER_TACLETS_OPTIONS_KEY1" : "USER_TACLETS_OFF",
            "USER_TACLETS_OPTIONS_KEY2" : "USER_TACLETS_OFF",
            "USER_TACLETS_OPTIONS_KEY3" : "USER_TACLETS_OFF",
            "VBT_PHASE" : "VBT_SYM_EX"
         }
     }
 }

""";

    public static final String EPILOG = """

\\proofObligation "#Proof Obligation Settings
#Tue Jan 16 17:45:53 CET 2024
contract=java_copy.util.DualPivotQuicksort[java_copy.util.DualPivotQuicksort\\:\\:sort([I,int,int,[I,int,int)].JML normal_behavior operation contract.0
name=java_copy.util.DualPivotQuicksort[java_copy.util.DualPivotQuicksort\\:\\:sort([I,int,int,[I,int,int)].JML normal_behavior operation contract.0
class=de.uka.ilkd.key.proof.init.FunctionalOperationContractPO
";

""";

}
