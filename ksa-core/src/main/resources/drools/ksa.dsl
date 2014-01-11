# The KSA DSL definition

# Commonly used DSL definitions

[keyword][]and = &&
[keyword][]eq = ==
[keyword][]lt = <
[keyword][]gt = >
[keyword][]lte = <=
[keyword][]gte = >=

# LHS definitions

[when][]\({constraints}\) = context : BrmContext({constraints})
[when][]Context is initialized = isInitialized()
[when][]Account ID is "{userId}" = account.id == "{userId}"
[when][]ATP is "{atpIds}" = CommonUtils.containsAny(atpIds, "{atpIds}", ",")
[when][]Transaction type is "{transactionTypeIds}" = CommonUtils.containsAny(transactionTypeIds, "{transactionTypeIds}", ",")
[when][]Hold issue is "{holdIssueNames}" = CommonUtils.containsAny(holdIssueNames, "{holdIssueNames}", ",")
[when][]Permission is "{permissionNames}" = CommonUtils.containsAny(permissionNames, "{permissionNames}", ",")
[when][]Account type is "{accountTypeNames}" = CommonUtils.containsAny(accountTypeNames, "{accountTypeNames}", ",")
[when][]Transaction amount is ${amount} = new BigDecimal({amount}).toString().equals(transactionAmount.toString())
[when][]Transaction amount > ${amount} = transactionAmount.doubleValue() > {amount}
[when][]Transaction amount < ${amount} = transactionAmount.doubleValue() < {amount}
[when][]Flag type is "{flagTypeCodes}" = CommonUtils.containsAny(flagTypeCodes, "{flagTypeCodes}")

# RHS definitions
[then][]Use code "{debitTypeId}" to charge ${debitAmount} = context.getTransactionService().createCharge("{debitTypeId}",context.getAccount().getId(), new Date(), new BigDecimal({debitAmount}));
[then][]Use code "{creditTypeId}" to credit ${creditAmount} = context.getTransactionService().createPayment("{creditTypeId}",context.getAccount().getId(), new Date(), new BigDecimal({creditAmount}));

########################################################################################################################

# FEE MANAGEMENT DSL definitions

# LHS definitions
[when][]account key "{key}" is "{value}" = fmService.compareAccountKeyPair("{key}","{value}","==",context)
[when][]account key "{key}" is not "{value}" = fmService.compareAccountKeyPair("{key}","{value}","!=",context)
[when][]account key "{key}" gt {value} = fmService.compareAccountKeyPair("{key}","{value}",">",context)
[when][]account key "{key}" gte {value} = fmService.compareAccountKeyPair("{key}","{value}",">=",context)
[when][]account key "{key}" lt {value} = fmService.compareAccountKeyPair("{key}","{value}","<",context)
[when][]account key "{key}" lte {value} = fmService.compareAccountKeyPair("{key}","{value}","<=",context)
[when][]session key "{key}" is "{value}" = fmService.compareSessionKeyPair("{key}","{value}","==",context)
[when][]session key "{key}" is not "{value}" = fmService.compareSessionKeyPair("{key}","{value}","!=",context)
[when][]session key "{key}" gt {value} = fmService.compareSessionKeyPair("{key}","{value}",">",context)
[when][]session key "{key}" gte {value} = fmService.compareSessionKeyPair("{key}","{value}",">=",context)
[when][]session key "{key}" lt {value} = fmService.compareSessionKeyPair("{key}","{value}","<",context)
[when][]session key "{key}" lte {value} = fmService.compareSessionKeyPair("{key}","{value}","<=",context)
[when][]signup key "{key}" is "{value}" = fmService.compareSignupKeyPair("{key}","{value}","==",context)
[when][]signup key "{key}" is not "{value}" = fmService.compareSignupKeyPair("{key}","{value}","!=",context)
[when][]signup key "{key}" gt {value} = fmService.compareSignupKeyPair("{key}","{value}",">",context)
[when][]signup key "{key}" gte {value} = fmService.compareSignupKeyPair("{key}","{value}",">=",context)
[when][]signup key "{key}" lt {value} = fmService.compareSignupKeyPair("{key}","{value}","<",context)
[when][]signup key "{key}" lte {value} = fmService.compareSignupKeyPair("{key}","{value}","<=",context)
[when][]signup rate key "{key}" is "{value}" = fmService.compareSignupRateKeyPair("{key}","{value}","==",context)
[when][]signup rate key "{key}" is not "{value}" = fmService.compareSignupRateKeyPair("{key}","{value}","!=",context)
[when][]signup rate key "{key}" gt {value} = fmService.compareSignupRateKeyPair("{key}","{value}",">",context)
[when][]signup rate key "{key}" gte {value} = fmService.compareSignupRateKeyPair("{key}","{value}",">=",context)
[when][]signup rate key "{key}" lt {value} = fmService.compareSignupRateKeyPair("{key}","{value}","<",context)
[when][]signup rate key "{key}" lte {value} = fmService.compareSignupRateKeyPair("{key}","{value}","<=",context)
[when][]account type is "{accountTypeCode}" = fmService.compareAccountType("{accountTypeCode}","==",context)
[when][]account type is not "{accountTypeCode}" = fmService.compareAccountType("{accountTypeCode}","!=",context)
[when][]account status is "{accountStatusCode}" = fmService.compareAccountStatus("{accountStatusCode}","==",context)
[when][]account status is not "{accountStatusCode}" = fmService.compareAccountStatus("{accountStatusCode}","!=",context)
[when][]session atp is "{atpId}" = fmService.compareSessionAtp("{atpId}","==",context)
[when][]session atp is not "{atpId}" = fmService.compareSessionAtp("{atpId}","!=",context)
[when][]account has flag "{flagTypeCode}" = fmService.accountHasFlag("{flagTypeCode}",null,"==",context)
[when][]account has flag "{flagTypeCode}" with severity {severity} = fmService.accountHasFlag("{flagTypeCode}",{severity},"==",context)
[when][]account has flag "{flagTypeCode}" with severity above {severity} = fmService.accountHasFlag("{flagTypeCode}",{severity},">",context)
[when][]account has flag "{flagTypeCode}" with severity below {severity} = fmService.accountHasFlag("{flagTypeCode}",{severity},"<",context)
[when][]account has applied hold "{holdIssueName}" = fmService.accountHasAppliedHold("{holdIssueName}",context)
[when][]signup operation is "{operations}" = fmService.compareSignupOperations("{operations}",context)
[when][]signup date is "{date}" = fmService.compareSignupEffectiveDate("{date}","==",context)
[when][]signup date is on atp milestone "{milestoneName}" = fmService.compareSignupEffectiveDateToAtpMilestone("{milestoneName}","==",context)
[when][]signup date is after atp milestone "{milestoneName}" = fmService.compareSignupEffectiveDateToAtpMilestone("{milestoneName}",">",context)
[when][]signup date is before atp milestone "{milestoneName}" = fmService.compareSignupEffectiveDateToAtpMilestone("{milestoneName}","<",context)
[when][]signup date is on or after atp milestone "{milestoneName}" = fmService.compareSignupEffectiveDateToAtpMilestone("{milestoneName}",">=",context)
[when][]signup date is on or before atp milestone "{milestoneName}" = fmService.compareSignupEffectiveDateToAtpMilestone("{milestoneName}","<=",context)
[when][]units of rates "{rateCodes}" is taken units of rates "{takenRateCodes}" = fmService.compareNumberOfTakenUnits("{rateCodes}","{takenRateCodes}","==",context)
[when][]units of rates "{rateCodes}" gt taken units of rates "{takenRateCodes}" = fmService.compareNumberOfTakenUnits("{rateCodes}","{takenRateCodes}",">",context)
[when][]units of rates "{rateCodes}" gte taken units of rates "{takenRateCodes}" = fmService.compareNumberOfTakenUnits("{rateCodes}","{takenRateCodes}",">=",context)
[when][]units of rates "{rateCodes}" lt taken units of rates "{takenRateCodes}" = fmService.compareNumberOfTakenUnits("{rateCodes}","{takenRateCodes}","<",context)
[when][]units of rates "{rateCodes}" lte taken units of rates "{takenRateCodes}" = fmService.compareNumberOfTakenUnits("{rateCodes}","{takenRateCodes}","<=",context)
[when][]amount of rates "{rateCodes}" is taken amount of rates "{takenRateCodes}" = fmService.compareAmountOfTakenSignups("{rateCodes}","{takenRateCodes}","==",context)
[when][]amount of rates "{rateCodes}" gt taken amount of rates "{takenRateCodes}" = fmService.compareAmountOfTakenSignups("{rateCodes}","{takenRateCodes}",">",context)
[when][]amount of rates "{rateCodes}" gte taken amount of rates "{takenRateCodes}" = fmService.compareAmountOfTakenSignups("{rateCodes}","{takenRateCodes}",">=",context)
[when][]amount of rates "{rateCodes}" lt taken amount of rates "{takenRateCodes}" = fmService.compareAmountOfTakenSignups("{rateCodes}","{takenRateCodes}","<",context)
[when][]amount of rates "{rateCodes}" lte taken amount of rates "{takenRateCodes}" = fmService.compareAmountOfTakenSignups("{rateCodes}","{takenRateCodes}","<=",context)
[when][]number of units is {units} with rates "{rateCodes}", types "{rateTypeCodes}", signup operations "{signupOperations}" = fmService.compareNumberOfUnits({units},"{rateCodes}","{rateTypeCodes}","{signupOperations}","==",context)
[when][]number of units gt {units} with rates "{rateCodes}", types "{rateTypeCodes}", signup operations "{signupOperations}" = fmService.compareNumberOfUnits({units},"{rateCodes}","{rateTypeCodes}","{signupOperations}",">",context)
[when][]number of units gte {units} with rates "{rateCodes}", types "{rateTypeCodes}", signup operations "{signupOperations}" = fmService.compareNumberOfUnits({units},"{rateCodes}","{rateTypeCodes}","{signupOperations}",">=",context)
[when][]number of units lt {units} with rates "{rateCodes}", types "{rateTypeCodes}", signup operations "{signupOperations}" = fmService.compareNumberOfUnits({units},"{rateCodes}","{rateTypeCodes}","{signupOperations}","<",context)
[when][]number of units lte {units} with rates "{rateCodes}", types "{rateTypeCodes}", signup operations "{signupOperations}" = fmService.compareNumberOfUnits({units},"{rateCodes}","{rateTypeCodes}","{signupOperations}","<=",context)
[when][]number of taken units is {units} with rates "{rateCodes}", types "{rateTypeCodes}", signup operations "{signupOperations}" = fmService.compareNumberOfTakenUnits({units},"{rateCodes}","{rateTypeCodes}","{signupOperations}","==",context)
[when][]number of taken units gt {units} with rates "{rateCodes}", types "{rateTypeCodes}", signup operations "{signupOperations}" = fmService.compareNumberOfTakenUnits({units},"{rateCodes}","{rateTypeCodes}","{signupOperations}",">",context)
[when][]number of taken units gte {units} with rates "{rateCodes}", types "{rateTypeCodes}", signup operations "{signupOperations}" = fmService.compareNumberOfTakenUnits({units},"{rateCodes}","{rateTypeCodes}","{signupOperations}",">=",context)
[when][]number of taken units lt {units} with rates "{rateCodes}", types "{rateTypeCodes}", signup operations "{signupOperations}" = fmService.compareNumberOfTakenUnits({units},"{rateCodes}","{rateTypeCodes}","{signupOperations}","<",context)
[when][]number of taken units lte {units} with rates "{rateCodes}", types "{rateTypeCodes}", signup operations "{signupOperations}" = fmService.compareNumberOfTakenUnits({units},"{rateCodes}","{rateTypeCodes}","{signupOperations}","<=",context)
[when][]number of signups is {units} with rates "{rateCodes}", types "{rateTypeCodes}", signup operations "{signupOperations}" = fmService.compareNumberOfSignups({units},"{rateCodes}","{rateTypeCodes}","{signupOperations}","==",context)
[when][]number of signups gt {units} with rates "{rateCodes}", types "{rateTypeCodes}", signup operations "{signupOperations}" = fmService.compareNumberOfSignups({units},"{rateCodes}","{rateTypeCodes}","{signupOperations}",">",context)
[when][]number of signups gte {units} with rates "{rateCodes}", types "{rateTypeCodes}", signup operations "{signupOperations}" = fmService.compareNumberOfSignups({units},"{rateCodes}","{rateTypeCodes}","{signupOperations}",">=",context)
[when][]number of signups lt {units} with rates "{rateCodes}", types "{rateTypeCodes}", signup operations "{signupOperations}" = fmService.compareNumberOfSignups({units},"{rateCodes}","{rateTypeCodes}","{signupOperations}","<",context)
[when][]number of signups lte {units} with rates "{rateCodes}", types "{rateTypeCodes}", signup operations "{signupOperations}" = fmService.compareNumberOfSignups({units},"{rateCodes}","{rateTypeCodes}","{signupOperations}","<=",context)


# RHS definitions
[then][]on session fire "{ruleSetName}" rule set = context.getFmService().fireSessionRuleSet("{ruleSetName}",context);
[then][]on each session signup fire "{ruleSetName}" rule set = context.getFmService().fireSignupRuleSet("{ruleSetName}",context);
[then][]set account key "{key}" to "{value}" = context.getFmService().setAccountKeyPair("{key}","{value}",context);
[then][]set session key "{key}" to "{value}" = context.getFmService().setSessionKeyPair("{key}","{value}",context);
[then][]set session key "{key}" to number of units where signup is taken = context.getFmService().setSessionKeyPairToUnitNumberWithSignupMethod("{key}","isTaken",context);
[then][]set session key "{key}" to number of units where signup operation is "{includedOperations}" minus "{excludedOperations}" = context.getFmService().setSessionKeyPairToUnitNumber("{key}","{includedOperations}","{excludedOperations}",context);
[then][]set signup key "{key}" to "{value}" = context.getFmService().setSignupKeyPair("{key}","{value}",context);
[then][]mark signup as taken = context.getFmService().setSignupTaken(true,context);
[then][]mark signup as not taken = context.getFmService().setSignupTaken(false,context);
[then][]mark signup as complete = context.getFmService().setSignupComplete(true,context);
[then][]mark signup as not complete = context.getFmService().setSignupComplete(false,context);
[then][]mark all signups as taken = context.getFmService().setSessionSignupsTaken(true,null,context);
[then][]mark all signups as not taken = context.getFmService().setSessionSignupsTaken(false,null,context);
[then][]mark all signups as complete = context.getFmService().setSessionSignupsComplete(true,null,context);
[then][]mark all signups as not complete = context.getFmService().setSessionSignupsComplete(false,null,context);
[then][]mark all signups with operation "{operations}" as taken = context.getFmService().setSessionSignupsTaken(true,"{operations}",context);
[then][]mark all signups with operation "{operations}" as not taken = context.getFmService().setSessionSignupsTaken(false,"{operations}",context);
[then][]mark all signups with operation "{operations}" as complete = context.getFmService().setSessionSignupsComplete(true,"{operations}",context);
[then][]mark all signups with operation "{operations}" as not complete = context.getFmService().setSessionSignupsComplete(false,"{operations}",context);
[then][]mark preceding offerings as complete = context.getFmService().setPrecedingOfferingsComplete(true,null,context);
[then][]mark preceding offerings as not complete = context.getFmService().setPrecedingOfferingsComplete(false,null,context);
[then][]mark preceding offerings with operation "{operations}" as complete = context.getFmService().setPrecedingOfferingsComplete(true,"{operations}",context);
[then][]mark preceding offerings as taken = context.getFmService().setPrecedingOfferingsTaken(true,null,context);
[then][]mark preceding offerings as not taken = context.getFmService().setPrecedingOfferingsTaken(false,null,context);
[then][]mark preceding offerings with operation "{operations}" as taken = context.getFmService().setPrecedingOfferingsTaken(true,"{operations}",context);
[then][]add signup rate "{rateCode}", "{rateSubCode}" = context.getFmService().addRate("{rateCode}","{rateSubCode}",context);
[then][]replace signup rates "{rateCodes}", "{rateSubCodes}" with "{newRateCode}", "{newRateSubCode}" = context.getFmService().replaceRates("{rateCodes}","{rateSubCodes}","{newRateCode}","{newRateSubCode}",context);
[then][]remove signup rates "{rateCodes}" = context.getFmService().removeRates("{rateCodes}",null,null,context);
[then][]remove signup rates "{rateCodes}" with type "{rateTypeCodes}", catalog "{rateCatalogCodes}" = context.getFmService().removeRates("{rateCodes}","{rateTypeCodes}","{rateCatalogCodes}",context);
[then][]remove rates "{rateCodes}" from preceding offerings = context.getFmService().removeRatesFromPrecedingOfferings("{rateCodes}",null,null,context);
[then][]remove rates "{rateCodes}" from preceding offerings with type "{rateTypeCodes}", catalog "{rateCatalogCodes}" = context.getFmService().removeRatesFromPrecedingOfferings("{rateCodes}","{rateTypeCodes}","{rateCatalogCodes}",context);
[then][]add tags "{tagCodes}" to manifests with rates "{rateCodes}", types "{rateTypeCodes}", catalogs "{rateCatalogCodes}" = context.getFmService().addTagsToManifests("{tagCodes}","{rateCodes}","{rateTypeCodes}","{rateCatalogCodes}",context);
[then][]add tags "{tagCodes}" to manifests with id "{internalChargeId}" = context.getFmService().addTagsToManifests("{tagCodes}","{internalChargeId}",context);
[then][]add rollup "{rollupCode}" to manifests with rates "{rateCodes}", types "{rateTypeCodes}", catalogs "{rateCatalogCodes}" = context.getFmService().addRollupToManifests("{rollupCode}","{rateCodes}","{rateTypeCodes}","{rateCatalogCodes}",context);
[then][]add rollup "{rollupCode}" to manifests with id "{internalChargeId}" = context.getFmService().addRollupToManifests("{rollupCode}","{internalChargeId}",context);
[then][]set effective date to "{effectiveDate}" on manifests with rates "{rateCodes}", types "{rateTypeCodes}", catalogs "{rateCatalogCodes}" = context.getFmService().setManifestEffectiveDate("{effectiveDate}","{rateCodes}","{rateTypeCodes}","{rateCatalogCodes}",context);
[then][]set effective date to "{effectiveDate}" on manifests with id "{internalChargeId}" = context.getFmService().setManifestEffectiveDate("{effectiveDate}","{internalChargeId}",context);
[then][]set recognition date to "{recognitionDate}" on manifests with rates "{rateCodes}", types "{rateTypeCodes}", catalogs "{rateCatalogCodes}" = context.getFmService().setManifestRecognitionDate("{recognitionDate}","{rateCodes}","{rateTypeCodes}","{rateCatalogCodes}",context);
[then][]set recognition date to "{recognitionDate}" on manifests with id "{internalChargeId}" = context.getFmService().setManifestRecognitionDate("{recognitionDate}","{internalChargeId}",context);
[then][]set amount to ${amount} on manifests with rates "{rateCodes}", types "{rateTypeCodes}", catalogs "{rateCatalogCodes}" = context.getFmService().setManifestAmount(new BigDecimal({amount}),"{rateCodes}","{rateTypeCodes}","{rateCatalogCodes}",context);
[then][]set amount to ${amount} on manifests with id "{internalChargeId}" = context.getFmService().setManifestAmount(new BigDecimal({amount}),"{internalChargeId}",context);
[then][]set gl account to "{glAccountId}" on manifests with rates "{rateCodes}", types "{rateTypeCodes}", catalogs "{rateCatalogCodes}" = context.getFmService().setManifestGlAccount("{glAccountId}","{rateCodes}","{rateTypeCodes}","{rateCatalogCodes}",context);
[then][]set gl account to "{glAccountId}" on manifests with id "{internalChargeId}" = context.getFmService().setManifestGlAccount("{glAccountId}","{internalChargeId}",context);
[then][]charge rates "{rateCodes}", "{rateSubCodes}" with types "{rateTypeCodes}", catalogs "{rateCatalogCodes}" = context.getFmService().chargeSignupRates("{rateCodes}","{rateSubCodes}","{rateTypeCodes}","{rateCatalogCodes}",context);
[then][]charge incidental rate "{rateCode}", "{rateSubCode}" with {units} units in amount of ${amount} using id "{internalChargeId}" = context.getFmService().chargeIncidentalRate("{rateCode}","{rateSubCode}","{internalChargeId}",{units},new BigDecimal({amount}),context);
[then][]charge incidental rate "{rateCode}", "{rateSubCode}" with {units} units using id "{internalChargeId}" = context.getFmService().chargeIncidentalRate("{rateCode}","{rateSubCode}","{internalChargeId}",{units},null,context);
[then][]charge incidental rate "{rateCode}", "{rateSubCode}" using id "{internalChargeId}" = context.getFmService().chargeIncidentalRate("{rateCode}","{rateSubCode}","{internalChargeId}",0,null,context);
[then][]discount rate "{rateCode1}", "{rateSubCode1}" by ${amount} using "{rateCode2}", "{rateSubCode2}" = context.getFmService().discountManifestRate("{rateCode1}","{rateSubCode1}","{rateCode2}","{rateSubCode2}",new BigDecimal({amount}),false,context);
[then][]discount rate "{rateCode1}", "{rateSubCode1}" by ${amount} = context.getFmService().discountManifestRate("{rateCode1}","{rateSubCode1}","{rateCode1}","{rateSubCode1}",new BigDecimal({amount}),false,context);
[then][]discount rate "{rateCode1}", "{rateSubCode1}" by {percentage}% using "{rateCode2}", "{rateSubCode2}" = context.getFmService().discountManifestRate("{rateCode1}","{rateSubCode1}","{rateCode2}","{rateSubCode2}",new BigDecimal({amount}),true,context);
[then][]discount rate "{rateCode1}", "{rateSubCode1}" by {percentage}% = context.getFmService().discountManifestRate("{rateCode1}","{rateSubCode1}","{rateCode1}","{rateSubCode1}",new BigDecimal({amount}),true,context);



########################################################################################################################

# ACCOUNT BLOCKING DSL definitions
# Assumption: transactionTypeIds, atpIds, holdIssueNames, permissionNames are global parameters

# RHS definitions
[then][]Apply block = blockNames.add(drools.getRule().getName());

# PAYMENT BOUNCING DSL definitions

# RHS definitions
[then][]Use flag type "{flagTypeCode}", access level "{accessLevelCode}", severity {severity} to create flag expiring in {days} days = context.getInformationService().createFlag(context.getAccount().getId(), "{flagTypeCode}", "{accessLevelCode}", {severity}, new Date(), CalendarUtils.addCalendarDays(new Date(), {days}));
[then][]Use access level "{accessLevelCode}" to create alert "{alertText}" expiring in {days} days = context.getInformationService().createAlert(context.getAccount().getId(), "{alertText}", "{accessLevelCode}", new Date(), CalendarUtils.addCalendarDays(new Date(), {days}));
[then][]Use access level "{accessLevelCode}" to create memo "{memoText}" expiring in {days} days = context.getInformationService().createMemo(context.getAccount().getId(), "{memoText}", "{accessLevelCode}", new Date(), CalendarUtils.addCalendarDays(new Date(), {days}), null);
[then][]Use hold issue type "{holdIssueType}", hold issue name "{holdIssueName}" to create hold "{holdName}" with description "{holdDescription}" expiring in {days} days = context.getHoldService().createAppliedHold(context.getAccount().getId(), "{holdIssueType}", "{holdIssueName}", "{holdName}", "{holdDescription}", new Date(), CalendarUtils.addCalendarDays(new Date(), {days}));

# PAYMENT APPLICATION DSL definitions

# LHS definitions

# RHS definitions
[then][]Set global variable "{globalVariable}" to "{attributeValue}" = context.getBrmPaymentService().setGlobalVariableToAttributeValue("{globalVariable}", "{attributeValue}", context);
[then][]Initialize list of transactions as "{transactions}" = context.getAttributes().put("{transactions}", TransactionUtils.newTransactionList());
[then][]Initialize list of GL transactions as "{glTransactions}" = context.getAttributes().put("{glTransactions}", TransactionUtils.newGlTransactionList());
[then][]Get list of transactions from "{startDate}" to "{endDate}", store result in "{transactions}" = context.getBrmPaymentService().getActiveTransactions("{startDate}", "{endDate}", "{transactions}", context);
[then][]Remove allocations from "{transactions}", add result to "{glTransactions}" = context.getBrmPaymentService().removeAllocations("{transactions}", "{glTransactions}", context);
[then][]Remove "{entries}" from "{transactions}" = context.getBrmPaymentService().removeTransactions("{entries}", "{transactions}", context);
[then][]Allocate reversals for "{transactions}", add result to "{glTransactions}" = context.getBrmPaymentService().allocateReversals("{transactions}", "{glTransactions}", context);
[then][]Apply payments for "{transactions}", add result to "{glTransactions}" = context.getBrmPaymentService().applyPayments("{transactions}", "{glTransactions}", context);
[then][]Apply payments with maximum amount ${amount} for "{transactions}", add result to "{glTransactions}" = context.getBrmPaymentService().applyPayments(new BigDecimal({amount}), "{transactions}", "{glTransactions}", context);
[then][]Calculate matrix scores for "{transactions}" = context.getBrmPaymentService().calculateMatrixScores("{transactions}", context);
[then][]Summarize GL transactions "{glTransactions}" = context.getBrmPaymentService().summarizeGlTransactions("{glTransactions}", context);

# Transaction sorting
[then][]Sort "{transactions}" by matrix score in ascending order = context.getBrmPaymentService().sortByMatrixScore("{transactions}", true, context);
[then][]Sort "{transactions}" by matrix score in descending order = context.getBrmPaymentService().sortByMatrixScore("{transactions}", false, context);
[then][]Sort "{transactions}" by priority in ascending order = context.getBrmPaymentService().sortByPriority("{transactions}", true, context);
[then][]Sort "{transactions}" by priority in descending order = context.getBrmPaymentService().sortByPriority("{transactions}", false, context);
[then][]Sort "{transactions}" by effective date in ascending order = context.getBrmPaymentService().sortByEffectiveDate("{transactions}", true, context);
[then][]Sort "{transactions}" by effective date in descending order = context.getBrmPaymentService().sortByEffectiveDate("{transactions}", false, context);

# Transaction filters
[then][]Get payments from "{transactions}" for {year} year, store result in "{payments}" = context.getBrmPaymentService().filterTransactions(TransactionTypeValue.PAYMENT, {year}, "{transactions}", "{payments}", context);
[then][]Get payments from "{transactions}", store result in "{payments}" = context.getBrmPaymentService().filterTransactions(TransactionTypeValue.PAYMENT, "{transactions}", "{payments}", context);
[then][]Get payments with tag "{tag}" from "{transactions}" for {year} year, store result in "{payments}" = context.getBrmPaymentService().filterTransactions(TransactionTypeValue.PAYMENT, {year}, "{tag}", "{transactions}", "{payments}", context);
[then][]Get charges from "{transactions}" for {year} year, store result in "{charges}" = context.getBrmPaymentService().filterTransactions(TransactionTypeValue.CHARGE, {year}, "{transactions}", "{charges}", context);
[then][]Get charges from "{transactions}", store result in "{charges}" = context.getBrmPaymentService().filterTransactions(TransactionTypeValue.CHARGE, "{transactions}", "{charges}", context);
[then][]Get charges with tag "{tag}" from "{transactions}" for {year} year, store result in "{charges}" = context.getBrmPaymentService().filterTransactions(TransactionTypeValue.CHARGE, {year}, "{tag}", "{transactions}", "{charges}", context);

