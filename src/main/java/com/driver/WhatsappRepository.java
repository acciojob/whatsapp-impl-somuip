package com.driver;

import java.util.*;

import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

@Repository
public class WhatsappRepository {

    //Assume that each user belongs to at most one group
    //You can use the below mentioned hashmaps or delete these and create your own.

    // db for userMap to store(mobile, user)
    private HashMap<String, User> userMap = new HashMap<>();

    // hashmap to store ( Admin(User) , List<Users> in the group)
    private HashMap<User, List<User>> adminHashMap = new HashMap<>();

    // db for groups
    private HashMap<Group, List<User>> groupUserMap;
    private HashMap<Group, List<Message>> groupMessageMap;
    private HashMap<Message, User> senderMap;

    // db for adminmap
    private HashMap<Group, User> adminMap;
    private HashSet<String> userMobile;
    private int customGroupCount;
    private int messageId;

    public WhatsappRepository(){
        this.groupMessageMap = new HashMap<Group, List<Message>>();
        this.groupUserMap = new HashMap<Group, List<User>>();
        this.senderMap = new HashMap<Message, User>();
        this.adminMap = new HashMap<Group, User>();
        this.userMobile = new HashSet<>();
        this.customGroupCount = 0;
        this.messageId = 0;
    }

    public String createUser(String name, String mobile) throws Exception{
        if(!userMap.containsKey(mobile)){
            User user = new User(name, mobile);
            userMap.put(mobile, user);
            return "Success";
        }
        else{
            throw new Exception("User already exist");
        }
    }

    public Group createGroup(List<User> users){
        Group group = new Group();

        if(users.size() == 2){
            group.setName(users.get(1).getName());
            group.setNumberOfParticipants(2);

        }
        else{
            customGroupCount += 1;
            group.setName("Group"+customGroupCount);
            group.setNumberOfParticipants(users.size());
        }

        adminMap.put(group, users.get(0));
        adminHashMap.put(users.get(0), users);
        return group;
    }

    public int createMessage(String content){
        Message message = new Message();
        messageId += 1;
        return messageId;
    }

    public int sendMessages(Message message, User sender, Group group) throws Exception{
       if(groupUserMap.containsKey(group)){
           List<User> users = groupUserMap.get(group);

           if(senderMap.containsValue(sender)){
               List<Message> messages;

               if(groupMessageMap.containsKey(group)){
                   messages = groupMessageMap.get(group);
               }
               else{
                   messages = new ArrayList<>();
               }

               messages.add(message);
               groupMessageMap.put(group, messages);
               return groupMessageMap.size();
           }
           else {
               throw new Exception("You are not allowed to send message");
           }
       }
       else{
           throw new Exception("Group does not exist");
       }
    }

    public String changeAdmin(User approver, User user, Group group) throws Exception{
        if(groupUserMap.containsKey(group)){
            List<User> users = groupUserMap.get(group);
            if(users.get(0).equals(user)){
                if(users.contains(user)){
                    users.set(0, user);
                    return "SUCCESS";
                }
                else{
                    throw new Exception("User is not a participant");
                }
            }
            else{
                throw new Exception("Approver does not have rights");
            }
        }
        else{
            throw new Exception("Group does not exist");
        }
    }
}
