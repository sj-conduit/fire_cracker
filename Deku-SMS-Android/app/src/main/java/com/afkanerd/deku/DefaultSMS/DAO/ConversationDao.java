package com.afkanerd.deku.DefaultSMS.DAO;

import android.content.Context;

import androidx.paging.PagingSource;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.afkanerd.deku.DefaultSMS.Models.Conversations.Conversation;
import com.afkanerd.deku.Datastore;

import java.util.List;

@Dao
public interface ConversationDao {

    @Query("SELECT * FROM Conversation WHERE thread_id =:thread_id AND type IS NOT 3 ORDER BY date DESC")
    PagingSource<Integer, Conversation> get(String thread_id);

    @Query("SELECT * FROM Conversation WHERE thread_id =:thread_id AND type IS NOT 3 ORDER BY date DESC")
    List<Conversation> getDefault(String thread_id);

    @Query("SELECT * FROM Conversation WHERE thread_id =:thread_id ORDER BY date DESC")
    List<Conversation> getAll(String thread_id);

    @Query("SELECT * FROM Conversation WHERE thread_id =:threadId ORDER BY date DESC LIMIT 1")
    Conversation fetchLatestForThread(String threadId);

    @Query("SELECT * FROM Conversation ORDER BY date DESC")
    List<Conversation> getComplete();

    @Query("SELECT * FROM Conversation WHERE type = :type AND thread_id = :threadId ORDER BY date DESC")
    Conversation fetchTypedConversation(int type, String threadId);

    @Query("SELECT * FROM Conversation WHERE message_id =:message_id")
    Conversation getMessage(String message_id);

    @Insert
    long _insert(Conversation conversation);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insertAll(List<Conversation> conversationList);

    @Update
    int _update(Conversation conversation);

    @Update
    int update(List<Conversation> conversations);

    @Query("UPDATE Conversation SET read = :isRead WHERE thread_id = :threadId AND read = 0")
    int updateRead(boolean isRead, String threadId);

    @Query("DELETE FROM Conversation WHERE thread_id = :threadId")
    int delete(String threadId);

    @Query("DELETE FROM Conversation WHERE thread_id IN (:threadIds)")
    void deleteAll(List<String> threadIds);

    @Query("DELETE FROM Conversation WHERE type = :type AND thread_id = :thread_id")
    int _deleteAllType(int type, String thread_id);

    @Transaction
    default void deleteAllType(Context context, int type, String thread_id) {
        _deleteAllType(type, thread_id);
        Conversation conversation = fetchLatestForThread(thread_id);
        if(conversation != null)
            Datastore.getDatastore(context)
                    .threadedConversationsDao()
                    .insertThreadFromConversation(context, conversation);
    }

    @Delete
    int delete(Conversation conversation);

    @Delete
    int delete(List<Conversation> conversation);
}
