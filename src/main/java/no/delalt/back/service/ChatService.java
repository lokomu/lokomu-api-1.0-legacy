package no.delalt.back.service;

import com.github.f4b6a3.ulid.UlidCreator;
import no.delalt.back.model.dao.ChatMessageDAO;
import no.delalt.back.model.dao.ConversationDAO;
import no.delalt.back.model.dao.UserConversationDAO;
import no.delalt.back.model.dao.UserDAO;
import no.delalt.back.model.dto.input.ConversationCreationDTO;
import no.delalt.back.model.dto.input.NewMessageDTO;
import no.delalt.back.model.dto.output.ChatMessageDTO;
import no.delalt.back.model.dto.output.ConversationDTO;
import no.delalt.back.model.dto.output.UserDTO;
import no.delalt.back.model.object.UserPair;
import no.delalt.back.repository.ChatMessageRepository;
import no.delalt.back.repository.ConversationRepository;
import no.delalt.back.repository.UserConversationRepository;
import no.delalt.back.service.validation.CommunityRequestValidatonService;
import no.delalt.back.service.validation.UserCommunityValidationService;
import no.delalt.back.service.validation.UserValidationService;
import no.delalt.back.util.NanoIdGenerator;
import no.delalt.back.util.SanitizationUtil;
import no.delalt.back.util.SecurityUtil;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class ChatService {
  private final UserConversationRepository userConversationRepository;
  private final ConversationRepository conversationRepository;
  private final ChatMessageRepository chatMessageRepository;
  private final UserValidationService userValidationService;
  private final UserCommunityValidationService userCommunityValidationService;
  private final CommunityRequestValidatonService communityRequestValidatonService;

  public ChatService(
    UserConversationRepository userConversationRepository,
    ConversationRepository conversationRepository,
    ChatMessageRepository chatMessageRepository,
    UserValidationService userValidationService,
    UserCommunityValidationService userCommunityValidationService,
    CommunityRequestValidatonService communityRequestValidatonService
  ) {
    this.userConversationRepository = userConversationRepository;
    this.conversationRepository = conversationRepository;
    this.chatMessageRepository = chatMessageRepository;
    this.userValidationService = userValidationService;
    this.userCommunityValidationService = userCommunityValidationService;
    this.communityRequestValidatonService = communityRequestValidatonService;
  }

  // -------------------- Controller Methods --------------------

  /**
   * Handles the creation of a conversation.
   *
   * @param  userID                the ID of the user initiating the conversation
   * @param  conversationCreationDTO  the DTO containing the details of the conversation creation
   * @return                     the ID of the created conversation, if it already exists between the users
   *                              or the ID of the newly created conversation
   */
  @Transactional
  public String handleConversationCreation(
    String userID,
    ConversationCreationDTO conversationCreationDTO
  ) {
    validateDifferentUsers(userID, SecurityUtil.getAuthenticatedAccountID());

    UserPair userPair = validateAndOrderUsers(
      SecurityUtil.getAuthenticatedAccountID(),
      userID
    );

    if (validateConversationBetweenUsersExists(userPair)) {
      return getConversationIDBetweenUsers(userPair);
    }

    if (conversationCreationDTO.byRequest()) {
      userCommunityValidationService.validateUserIsAdminInCommunity(
        conversationCreationDTO.communityID()
      );
      communityRequestValidatonService.validateUserRequestExists(
        otherUserFromUserPair(userPair),
        conversationCreationDTO.communityID()
      );
    } else {
      userCommunityValidationService.validateCurrentUserIsMemberOfCommunity(
        conversationCreationDTO.communityID()
      );
      userCommunityValidationService.validateUserInCommunity(
        userID,
        conversationCreationDTO.communityID()
      );
    }

    return createConversation(userPair.user1(), userPair.user2());
  }

  /**
   * Retrieves the conversation ID between two users.
   *
   * @param  userID   the ID of the other user
   * @return               the conversation ID between the two users
   */
  @Transactional(readOnly = true)
  public String retrieveConversationID(String userID) {
    String currentUserID = SecurityUtil.getAuthenticatedAccountID();
    validateDifferentUsers(currentUserID, userID);

    UserPair userPair = validateAndOrderUsers(userID, currentUserID);

    return getConversationIDBetweenUsers(userPair);
  }

  /**
   * Checks if a conversation exists between two users.
   *
   * @param  userID   the ID of the other user
   * @return               true if a conversation exists between the two users, false otherwise
   */
  @Transactional(readOnly = true)
  public boolean checkIfConversationExists(String userID) {
    String currentUserID = SecurityUtil.getAuthenticatedAccountID();
    validateDifferentUsers(currentUserID, userID);

    UserPair userPair = validateAndOrderUsers(currentUserID, userID);

    return validateConversationBetweenUsersExists(userPair);
  }

  /**
   * Handles the sending of a message in a conversation.
   *
   * @param  conversationID  the ID of the conversation
   * @param  newMessageDTO   the DTO containing the details of the message
   * @return                 the ID of the created message
   */
  @Transactional
  public String handleSendMessage(
    String conversationID,
    NewMessageDTO newMessageDTO
  ) {
    String currentAccountID = SecurityUtil.getAuthenticatedAccountID();

    UserDAO senderUserDAO = userValidationService.validateUserExistsAndReturn(
      currentAccountID
    );

    ConversationDAO conversationDAO = validateConversationExistsAndReturn(
      conversationID
    );

    validateUserIsInConversation(currentAccountID, conversationDAO);

    return createMessage(senderUserDAO, conversationDAO, newMessageDTO);
  }

  /**
   * Handles the retrieval of all conversations for a given user.
   *
   * @return         a list of ConversationDTO objects representing the conversations
   */
  @Transactional(readOnly = true)
  public List<ConversationDTO> handleGetAllConversations() {
    String userID = SecurityUtil.getAuthenticatedAccountID();
    UserDAO userDAO = userValidationService.validateUserExistsAndReturn(userID);
    return getAllConversationsForUser(userDAO);
  }

  /**
   * Handles the retrieval of the latest chat messages for a given conversation ID and page number.
   *
   * @param  conversationID  the ID of the conversation
   * @param  page            the page number
   * @return                 a list of ChatMessageDTO objects representing the latest messages
   */
  @Transactional(readOnly = true)
  public List<ChatMessageDTO> handleGetLatestMessages(
    String conversationID,
    int page
  ) {
    ConversationDAO conversationDAO = validateConversationExistsAndReturn(
      conversationID
    );
    validateUserIsInConversation(
      SecurityUtil.getAuthenticatedAccountID(),
      conversationDAO
    );
    return getLatestMessages(conversationDAO, page);
  }

  // -------------------- Helper Methods --------------------

  /**
   * Validates the existence of a conversation and returns the corresponding ConversationDAO object.
   *
   * @param  conversationID  the ID of the conversation to be validated
   * @return                 the ConversationDAO object corresponding to the given conversation ID
   * @throws ResponseStatusException if the conversation does not exist
   */
  private ConversationDAO validateConversationExistsAndReturn(
    String conversationID
  )
    throws ResponseStatusException {
    return findConversationByConversationID(conversationID);
  }

  /**
   * Validates that the user is part of the conversation.
   *
   * @param  userID          the ID of the user
   * @param  conversationDAO the ConversationDAO object
   * @throws ResponseStatusException if the user is not part of the conversation
   */
  private static void validateUserIsInConversation(
    String userID,
    ConversationDAO conversationDAO
  ) {
    if (
      !(
        conversationDAO.getUser1().getUserID().equals(userID) ||
        conversationDAO.getUser2().getUserID().equals(userID)
      )
    ) throw new ResponseStatusException(
      HttpStatus.FORBIDDEN,
      "User not part of conversation"
    );
  }

  /**
   * Validates that the two users are different.
   *
   * @param  firstUserID     the ID of the first user
   * @param  secondUserID    the ID of the second user
   * @throws ResponseStatusException if the two users are the same
   */
  private static void validateDifferentUsers(
    String firstUserID,
    String secondUserID
  )
    throws ResponseStatusException {
    if (firstUserID.equals(secondUserID)) {
      throw new ResponseStatusException(
        HttpStatus.BAD_REQUEST,
        "Cannot create a conversation with the same user"
      );
    }
  }

  /**
   * Validates if a conversation between two users exists.
   *
   * @param  userPair  the UserPair object representing the two user DAOs
   * @return           true if a conversation exists between the two users, false otherwise
   */
  private boolean validateConversationBetweenUsersExists(UserPair userPair) {
    return conversationRepository
      .findByUser1AndUser2(userPair.user1(), userPair.user2())
      .isPresent();
  }

  /**
   * Returns the ConversationDAO object corresponding to the given conversation ID.
   *
   * @param  conversationID  the ID of the conversation
   * @return                 the ConversationDAO object
   * @throws ResponseStatusException if the conversation does not exist
   */
  private ConversationDAO findConversationByConversationID(
    String conversationID
  )
    throws ResponseStatusException {
    return conversationRepository
      .findById(conversationID)
      .orElseThrow(
        () ->
          new ResponseStatusException(
            HttpStatus.NOT_FOUND,
            "Conversation not found"
          )
      );
  }

  /**
   * Returns a list of ChatMessageDTO objects representing the latest messages in the conversation.
   *
   * @param  conversation  the ConversationDAO object
   * @param  page          the page number
   * @return               a list of ChatMessageDTO objects
   */
  private List<ChatMessageDTO> getLatestMessages(
    ConversationDAO conversation,
    int page
  ) {
    int pageSize = 10;
    Pageable pageable = PageRequest.of(
      page,
      pageSize,
      Sort.by("timeSent").descending()
    );
    List<ChatMessageDAO> chatMessageDAOs = chatMessageRepository.findByConversation(
      conversation,
      pageable
    );
    return chatMessageDAOs.stream().map(ChatMessageDTO::new).toList();
  }

  /**
   * Returns the ID of the conversation between two users.
   *
   * @param  userPair  the UserPair object representing the two user DAOs
   * @return           the ID of the conversation
   */
  private String getConversationIDBetweenUsers(UserPair userPair) {
    ConversationDAO conversation = conversationRepository
      .findByUser1AndUser2(userPair.user1(), userPair.user2())
      .orElseThrow(
        () ->
          new ResponseStatusException(
            HttpStatus.NOT_FOUND,
            "A conversation between these two users was not found."
          )
      );

    return conversation.getConversationID();
  }

  /**
   * Returns a list of ConversationDTO objects representing all conversations for a given user.
   *
   * @param  user  the UserDAO object
   * @return       a list of ConversationDTO objects
   */
  private List<ConversationDTO> getAllConversationsForUser(UserDAO user) {
    List<UserConversationDAO> userConversations = userConversationRepository.findByUser(
      user
    );
    if (userConversations.isEmpty()) {
      return Collections.emptyList();
    }
    return userConversations
      .stream()
      .map(
        userConversation -> {
          ConversationDAO conversation = userConversation.getConversation();

          UserDAO otherUser = conversation
              .getUser1()
              .getUserID()
              .equals(user.getUserID())
            ? conversation.getUser2()
            : conversation.getUser1();

          return new ConversationDTO(
            conversation.getConversationID(),
            new ChatMessageDTO(conversation.getLastMessage()),
            new UserDTO(otherUser)
          );
        }
      )
      .toList();
  }

  /**
   * Creates a new conversation between two users.
   *
   * @param  user1DAO  the UserDAO object representing the first user
   * @param  user2DAO  the UserDAO object representing the second user
   * @return           the ID of the created conversation
   */
  private String createConversation(UserDAO user1DAO, UserDAO user2DAO) {
    String newID = NanoIdGenerator.generateNanoID();
    ConversationDAO newConversation = new ConversationDAO(
      newID,
      user1DAO,
      user2DAO,
      null
    );

    conversationRepository.save(newConversation);

    UserConversationDAO user1ConversationDAO = new UserConversationDAO(
      user1DAO,
      newConversation
    );
    UserConversationDAO user2ConversationDAO = new UserConversationDAO(
      user2DAO,
      newConversation
    );

    userConversationRepository.saveAll(
      Arrays.asList(user1ConversationDAO, user2ConversationDAO)
    );

    return newConversation.getConversationID();
  }

  /**
   * Creates a new message in a conversation.
   *
   * @param  sender          the UserDAO object representing the sender
   * @param  conversationDAO the ConversationDAO object
   * @param  newMessageDTO   the DTO containing the details of the message
   * @return                 the ID of the created message
   */
  private String createMessage(
    UserDAO sender,
    ConversationDAO conversationDAO,
    NewMessageDTO newMessageDTO
  ) {
    String safeMessage = SanitizationUtil.sanitize(newMessageDTO.message());

    ChatMessageDAO chatMessageDAO = new ChatMessageDAO();

    chatMessageDAO.setTimeSent(System.currentTimeMillis());
    chatMessageDAO.setMessageID(UlidCreator.getMonotonicUlid().toString());
    chatMessageDAO.setSendingUser(sender);
    chatMessageDAO.setConversation(conversationDAO);
    chatMessageDAO.setText(safeMessage);

    chatMessageDAO = chatMessageRepository.save(chatMessageDAO);

    if (
      conversationDAO.getLastMessage() == null ||
      chatMessageDAO.getTimeSent() >
      conversationDAO.getLastMessage().getTimeSent()
    ) {
      conversationDAO.setLastMessage(chatMessageDAO);
      conversationRepository.save(conversationDAO);
    }

    return chatMessageDAO.getMessageID();
  }

  /**
   * Returns the not current UserDAO object based on the given UserPair object.
   *
   * @param  userPair  the UserPair object to retrieve the not current user from
   * @return           the current UserDAO object
   */
  private UserDAO otherUserFromUserPair(UserPair userPair) {
    if (
      SecurityUtil
        .getAuthenticatedAccountID()
        .equals(userPair.user1().getUserID())
    ) {
      return userPair.user2();
    } else return userPair.user1();
  }

  //TODO Single responsibility, no and
  /**
   * Validates and orders two user IDs.
   *
   * @param  firstUserID   the first user ID to validate and order
   * @param  secondUserID  the second user ID to validate and order
   * @return               a UserPair object with the validated and ordered user DAOs
   */
  public UserPair validateAndOrderUsers(
    String firstUserID,
    String secondUserID
  ) {
    UserDAO user1DAO;
    UserDAO user2DAO;

    if (firstUserID.compareTo(secondUserID) > 0) {
      user1DAO = userValidationService.validateUserExistsAndReturn(firstUserID);
      user2DAO =
        userValidationService.validateUserExistsAndReturn(secondUserID);
    } else {
      user1DAO =
        userValidationService.validateUserExistsAndReturn(secondUserID);
      user2DAO = userValidationService.validateUserExistsAndReturn(firstUserID);
    }

    return new UserPair(user1DAO, user2DAO);
  }
}
