package prorunvis;

public class KeYobligation {
    // will be replaced by gradle: @PROJECT_DIRECTORY@
    public static final String TRACED_SOURCE_FOLDER
        = "@PROJECT_DIRECTORY@/../quicksort/src/main/java/";

    public static final String PROLOG = """
\\profile "Java Profile";

\\settings {
"#Proof-Settings-Config-File
#Tue Apr 23 18:15:01 CEST 2024
[Labels]UseOriginLabels=true
[StrategyProperty]QUERYAXIOM_OPTIONS_KEY=QUERYAXIOM_ON
[SMTSettings]invariantForall=false
[Strategy]ActiveStrategy=JavaCardDLStrategy
[StrategyProperty]USER_TACLETS_OPTIONS_KEY1=USER_TACLETS_OFF
[StrategyProperty]QUANTIFIERS_OPTIONS_KEY=QUANTIFIERS_NON_SPLITTING_WITH_PROGS
[StrategyProperty]USER_TACLETS_OPTIONS_KEY2=USER_TACLETS_OFF
[Choice]DefaultChoices=JavaCard-JavaCard\\\\:off, Strings-Strings\\\\:on, assertions-assertions\\\\:safe, bigint-bigint\\\\:on, floatRules-floatRules\\\\:strictfpOnly, initialisation-initialisation\\\\:disableStaticInitialisation, intRules-intRules\\\\:arithmeticSemanticsIgnoringOF, integerSimplificationRules-integerSimplificationRules\\\\:full, javaLoopTreatment-javaLoopTreatment\\\\:efficient, mergeGenerateIsWeakeningGoal-mergeGenerateIsWeakeningGoal\\\\:off, methodExpansion-methodExpansion\\\\:modularOnly, modelFields-modelFields\\\\:treatAsAxiom, moreSeqRules-moreSeqRules\\\\:off, permissions-permissions\\\\:off, programRules-programRules\\\\:Java, reach-reach\\\\:on, runtimeExceptions-runtimeExceptions\\\\:ban, sequences-sequences\\\\:on, wdChecks-wdChecks\\\\:off, wdOperator-wdOperator\\\\:L
[StrategyProperty]LOOP_OPTIONS_KEY=LOOP_EXPAND
[StrategyProperty]INF_FLOW_CHECK_PROPERTY=INF_FLOW_CHECK_FALSE
[SMTSettings]UseBuiltUniqueness=false
[SMTSettings]explicitTypeHierarchy=false
[SMTSettings]instantiateHierarchyAssumptions=true
[StrategyProperty]NON_LIN_ARITH_OPTIONS_KEY=NON_LIN_ARITH_DEF_OPS
[SMTSettings]SelectedTaclets=
[StrategyProperty]DEP_OPTIONS_KEY=DEP_ON
[StrategyProperty]AUTO_INDUCTION_OPTIONS_KEY=AUTO_INDUCTION_OFF
[Strategy]MaximumNumberOfAutomaticApplications=100000
[StrategyProperty]STOPMODE_OPTIONS_KEY=STOPMODE_DEFAULT
[StrategyProperty]CLASS_AXIOM_OPTIONS_KEY=CLASS_AXIOM_FREE
[SMTSettings]useConstantsForBigOrSmallIntegers=true
[StrategyProperty]MPS_OPTIONS_KEY=MPS_MERGE
[Strategy]Timeout=-1
[StrategyProperty]QUERY_NEW_OPTIONS_KEY=QUERY_OFF
[SMTSettings]useUninterpretedMultiplication=true
[StrategyProperty]BLOCK_OPTIONS_KEY=BLOCK_EXPAND
[StrategyProperty]METHOD_OPTIONS_KEY=METHOD_EXPAND
[StrategyProperty]USER_TACLETS_OPTIONS_KEY3=USER_TACLETS_OFF
[SMTSettings]maxGenericSorts=2
[StrategyProperty]OSS_OPTIONS_KEY=OSS_ON
[StrategyProperty]SPLITTING_OPTIONS_KEY=SPLITTING_DELAYED
[SMTSettings]integersMinimum=-2147483645
[StrategyProperty]VBT_PHASE=VBT_SYM_EX
[SMTSettings]integersMaximum=2147483645
"
}

""";

    public static final String EPILOG = """

\\proofObligation "#Proof Obligation Settings
#Tue Jan 16 17:45:53 CET 2024
contract=quicksort.Quicksort[quicksort.Quicksort\\\\:\\\\:sort([I)].JML normal_behavior operation contract.0
name=quicksort.Quicksort[quicksort.Quicksort\\\\:\\\\:sort([I)].JML normal_behavior operation contract.0
class=de.uka.ilkd.key.proof.init.FunctionalOperationContractPO
";

""";

}
