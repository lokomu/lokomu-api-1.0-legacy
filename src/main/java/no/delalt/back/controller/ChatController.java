package no.delalt.back.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import no.delalt.back.configuration.validation.ValidID;
import no.delalt.back.model.dto.input.ConversationCreationDTO;
import no.delalt.back.model.dto.input.NewMessageDTO;
import no.delalt.back.model.dto.output.ChatMessageDTO;
import no.delalt.back.model.dto.output.ConversationDTO;
import no.delalt.back.response.bool.ConversationExistsBooleanResponse;
import no.delalt.back.response.id.ConversationIDResponse;
import no.delalt.back.response.id.MessageIDResponse;
import no.delalt.back.response.list.ChatMessageListResponse;
import no.delalt.back.response.list.ConversationListResponse;
import no.delalt.back.service.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@Validated
@RequestMapping("/chat")
public class ChatController {
  private final ChatService chatService;

  public ChatController(ChatService chatService) {
    this.chatService = chatService;
  }

  /**
   * Creates a conversation for the given user ID.
   *
   * @param  userID                    the ID of the user
   * @param  conversationCreationDTO   the DTO containing conversation creation details
   * @return                           the response entity containing the conversation ID
   * @throws ResponseStatusException   if an error occurs while handling the conversation creation
   */
  @Operation(summary = "Creates a conversation for the given user ID.")
  @PostMapping(
    path = "/conversation/{userID}",
    consumes = "application/json",
    produces = "application/json"
  )
  public ResponseEntity<ConversationIDResponse> createConversation(
    @PathVariable @ValidID @NotBlank String userID,
    @RequestBody @Valid ConversationCreationDTO conversationCreationDTO
  )
    throws ResponseStatusException {
    String conversationID = chatService.handleConversationCreation(
      userID,
      conversationCreationDTO
    );
    ConversationIDResponse conversationIDResponse = new ConversationIDResponse(
      conversationID
    );

    return ResponseEntity.ok(conversationIDResponse);
  }

  /**
   * Retrieves the conversation ID for the given other user ID.
   *
   * @param  userID               the ID of the other user
   * @return                           the response entity containing the conversation ID
   * @throws ResponseStatusException   if an error occurs while retrieving the conversation ID
   */
  @Operation(summary = "Retrieves the conversation ID for the given user IDs.")
  @GetMapping(path = "/conversation/{userID}", produces = "application/json")
  public ResponseEntity<ConversationIDResponse> getConversationID(
    @PathVariable @ValidID @NotBlank String userID
  ) {
    String conversationID = chatService.retrieveConversationID(userID);
    ConversationIDResponse conversationIDResponse = new ConversationIDResponse(
      conversationID
    );

    return ResponseEntity.ok(conversationIDResponse);
  }

  /**
   * Checks if a conversation exists between the given user IDs.
   *
   * @param  userID               the ID of the other user
   * @return                           the response entity containing the conversation existence
   * @throws ResponseStatusException   if an error occurs while checking the conversation existence
   */
  @Operation(
    summary = "Checks if a conversation exists between the given user IDs."
  )
  @GetMapping(
    path = "/conversation/exists/{userID}",
    produces = "application/json"
  )
  public ResponseEntity<ConversationExistsBooleanResponse> doesConversationExist(
    @PathVariable @ValidID @NotBlank String userID
  )
    throws ResponseStatusException {
    boolean conversationExists = chatService.checkIfConversationExists(userID);
    ConversationExistsBooleanResponse booleanResponse = new ConversationExistsBooleanResponse(
      conversationExists
    );

    return ResponseEntity.ok(booleanResponse);
  }

  /**
   * Sends a message to the given conversation ID.
   *
   * @param  conversationID            the ID of the conversation
   * @param  newMessageDTO             the DTO containing the message details
   * @return                           the response entity containing the message ID
   * @throws ResponseStatusException   if an error occurs while sending the message
   */
  @Operation(summary = "Sends a message to the given conversation ID.")
  @PostMapping(
    path = "/{conversationID}/message",
    consumes = "application/json",
    produces = "application/json"
  )
  public ResponseEntity<MessageIDResponse> sendMessage(
    @PathVariable @ValidID @NotBlank String conversationID,
    @RequestBody @Valid NewMessageDTO newMessageDTO
  )
    throws ResponseStatusException {
    String messageID = chatService.handleSendMessage(
      conversationID,
      newMessageDTO
    );
    MessageIDResponse messageIDResponse = new MessageIDResponse(messageID);

    return ResponseEntity.ok(messageIDResponse);
  }

  //TODO Later update it to be pageable
  /**
   * Retrieves all conversations for the given user ID.
   *
   * @return                           the response entity containing the list of conversations
   * @throws ResponseStatusException   if an error occurs while retrieving the conversations
   */
  @Operation(summary = "Retrieves all conversations for the given user ID.")
  @GetMapping(path = "/user/conversations", produces = "application/json")
  public ResponseEntity<ConversationListResponse> getAllConversations()
    throws ResponseStatusException {
    List<ConversationDTO> conversationDTOs = chatService.handleGetAllConversations();
    ConversationListResponse conversationListResponse = new ConversationListResponse(
      conversationDTOs
    );

    return ResponseEntity.ok(conversationListResponse);
  }

  /**
   * Retrieves a list of chat messages for a given conversation ID and page number.
   *
   * @param  conversationID  the ID of the conversation
   * @param  page            the page number of the chat messages
   * @return                 a response entity containing the list of chat messages
   * @throws ResponseStatusException  if there is an error retrieving the chat messages
   */
  @Operation(
    summary = "Retrieves a list of chat messages for a given conversation ID and page number."
  )
  @GetMapping(path = "/{conversationID}/{page}", produces = "application/json")
  public ResponseEntity<ChatMessageListResponse> getChatMessages(
    @PathVariable @ValidID @NotBlank String conversationID,
    @PathVariable @NotNull @PositiveOrZero int page
  )
    throws ResponseStatusException {
    List<ChatMessageDTO> chatMessageDTOs = chatService.handleGetLatestMessages(
      conversationID,
      page
    );
    ChatMessageListResponse chatMessageListResponse = new ChatMessageListResponse(
      chatMessageDTOs
    );

    return ResponseEntity.ok(chatMessageListResponse);
  }
}
