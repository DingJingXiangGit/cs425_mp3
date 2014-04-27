package model;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/*
    Helper class to track and access information about Chat members
 */
public class MemberIndexer {
	private Map<Integer, Member> _memberTable;
	private Map<Integer, Hashtable<Integer, Member>> _memberGroupTable;
	private static MemberIndexer _instance = new MemberIndexer();
	
	private MemberIndexer(){
		_memberTable = new Hashtable<Integer, Member>();
		_memberGroupTable = new Hashtable<Integer, Hashtable<Integer, Member>>();
	}
	
	public static MemberIndexer getInstance(){
		return _instance;
	}
	

	public Map<Integer, Member> getAllMembers(){
		return _memberTable;
	}
	
	public void addMembers(List<Member> members){
		//System.out.println("member list:");
		for(Member member: members){
			if(_memberGroupTable.containsKey(member._groupId) == false){
				_memberGroupTable.put(member._groupId, new  Hashtable<Integer, Member>());
			}
			_memberGroupTable.get(member._groupId).put(member._id, member);
			_memberTable.put(member._id, member);
			//System.out.println(member._id +": "+member._ip +": "+member._port);
		}
	}
	
	public int getGroupSize(int groupId){
		return _memberGroupTable.get(groupId).size();
	}
	
	public Member getById(int memberId){
		return _memberTable.get(memberId);
	}
	
	public Map<Integer, Member> getByGroupId(int groupId){
		return _memberGroupTable.get(groupId);
	}
}
