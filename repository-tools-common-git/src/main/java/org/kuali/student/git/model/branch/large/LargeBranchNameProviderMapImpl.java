package org.kuali.student.git.model.branch.large;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jgit.lib.ObjectId;
import org.kuali.student.git.model.branch.utils.GitBranchUtils;
import org.kuali.student.git.model.branch.utils.GitBranchUtils.ILargeBranchNameProvider;

public class LargeBranchNameProviderMapImpl implements ILargeBranchNameProvider {

	private Map<String, String>largeBranchNameMap = new HashMap<String, String>();
	
	@Override
	public String getBranchName(String longBranchId, long revision) {
		return largeBranchNameMap.get(longBranchId);
	}

	@Override
	public String storeLargeBranchName(String branchName, long revision) {
	
		ObjectId largeBranchNameId = GitBranchUtils.getBranchNameObjectId(branchName);
		
		this.largeBranchNameMap.put(largeBranchNameId.name(), branchName);
		
		return largeBranchNameId.name();
		
	}

	/**
	 * 
	 * @see java.util.Map#clear()
	 */
	public void clear() {
		largeBranchNameMap.clear();
	}

	
}
