package model;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/*
    Helper class to track and access information about Chat members
 */
public class MemberIndexer {
	private Map<Integer, Member> _memberTable;
	private static MemberIndexer _instance = new MemberIndexer();
	
	private MemberIndexer(){
		_memberTable = new Hashtable<Integer, Member>();
	}
	
	public static MemberIndexer getInstance(){
		return _instance;
	}
	

	public Map<Integer, Member> getAllMembers(){
		return _memberTable;
	}
	
	public void addMembers(List<Member> members){
		for(Member member: members){
			_memberTable.put(member._id, member);
		}
	}
	
	public Member getById(int memberId){
		return _memberTable.get(memberId);
	}
}
