let stompClient = null;
let meToken = null;
let meUserId = null;
let otherUserId = null;
let chatId = null;
let currentMessages = null;
let messageToChangeId = null;
let messageText = null;

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    }
    else {
        $("#conversation").hide();
    }
    $("#greetings").html("");
}

async function connect() {
    meToken = $("#userToken").val();
    let socket = new SockJS('/ws-chat', null, {});
    stompClient = Stomp.over(socket);
    stompClient.connect({
        'Authorization': 'Bearer ' + meToken
    }, frame => {
        setConnected(true);
        console.log('Connected: ' + frame);
        stompClient.subscribe('/chat/messages', obj => {
            showLoadingMessage();
        });
    });
    await init();
    await showLoadingMessage();
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

async function init() {
    meToken = $("#userToken").val();
    otherUserId = $("#userIdTwo").val();
    let chat = null;
    if(otherUserId) {
        chat = await fetch("http://localhost:8081/chat/get-chat",
            {
                method: 'POST',
                headers: {
                    'Authorization': 'Bearer ' + meToken,
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    'userId': otherUserId
                })
            })
            .then(response => response.json())
    } else {
        chat = await fetch("http://localhost:8081/chat/get-system-chat",
            {
                method: 'POST',
                headers: {
                    'Authorization': 'Bearer ' + meToken,
                    'Content-Type': 'application/json',
                }
            })
            .then(response => response.json())
    }
    console.log(chat)
    chatId = chat.id
    meUserId = chat.users.filter(item => item !== $("#userIdTwo").val())
    changeCurrentData()
}

function changeCurrentData() {
    $("#userIdOneShow").text(meUserId);
    $("#userIdTwoShow").text(otherUserId);
    $("#chatIdShow").text(chatId);
    $("#messageToChangeIdShow").text(messageToChangeId + " [" + messageText + "] ");
}

function sendText() {
    stompClient.send("/app/chat/sendMessage", {}, JSON.stringify(
        {
            'text': $("#text").val(),
            'chatRoomId': chatId
        }
    ));
}

function likeMessage(messageId,  isLike) {
    stompClient.send("/app/chat/likeMessage", {}, JSON.stringify(
        {
            'isLike': isLike,
            'messageId' : messageId
        }
    ));
}

function readMessage(messageId) {
    stompClient.send("/app/chat/readMessage", {}, JSON.stringify(
        {
            'messageId' : messageId
        }
    ));
}

function deleteMessage(messageId) {
    stompClient.send("/app/chat/deleteMessage", {}, JSON.stringify(
        {
            'messageId' : messageId
        }
    ));
}

function updateMessage() {
    if(messageToChangeId === null) {
        alert("select message!")
        return;
    }
    stompClient.send("/app/chat/updateMessage", {}, JSON.stringify(
        {
            'text': $("#text").val(),
            'messageId' : messageToChangeId
        }
    ));
}

async function showLoadingMessage() {
    console.log("meUserId        = " + meUserId);
    console.log("otherUserId = " + otherUserId);
    console.log("chatId      = " + chatId);
    let messages = await fetch("http://localhost:8081/chat/all-messages?chatId=" + chatId, {
        headers: {
            'Authorization': 'Bearer ' + meToken,
            'Content-Type': 'application/json',
        },
    })
        .then(response => response.json())
        .then(response => response.content);
    console.log(messages);
    console.log("meId" + meUserId)
    let createdBlock = false;
    let lastMessageFromIdLoading = null;
    let text = "";
    let append = (str) => {
        text += str;
    }
    currentMessages = messages;
    messages.forEach(message => {
        if (createdBlock === true && (lastMessageFromIdLoading === null || lastMessageFromIdLoading !== message["userId"])) {
            append("</div>");
            createdBlock = false;
        }
        if (lastMessageFromIdLoading === null || lastMessageFromIdLoading !== message["userId"]) {
            let username = message["userId"] == meUserId ? "Me" : "user " + message["userId"];
            append(
                "<div class=\"block_messages_from_person\">" +
                "<div class=\"sent_from\">" +
                username +
                "</div>"
            )
            createdBlock = true;
        }
        let isOwnStyle = meUserId == message["userId"] ? "message_own" : "message_not_own";

        let isLiked = message.messageLikes.length === 0 ? "" : "color:red;";
        let isRead = message.messageReads.length === 0 ? "" : "border-style: dotted;";
        let style = "style=\" " + isLiked + isRead + " \"";

        append(
            "<div class=\"new_line\" id=\"" + message.id  + "\">" +
            "<div class=\"" + isOwnStyle + " message_block\" " + style + ">"
        )
        append(message.text + "<br>");
        if(message.updated === true) {
            append("<b style='font-size: 0.5em;'>updated</b>")
        }
        append(
            "<div class=\"sent_at\" style=\"font-size: 0.5em\">" + message["sentAt"].substring(0,19) + "</div>"
        )
        append(
            "</div>" +
            "</div>"
        )
        lastMessageFromIdLoading = message["userId"];
    })
    $("#messages_block").empty().append(text)
    currentMessages.forEach(message => {
        if(message.userId != meUserId) {
            $("#" + message.id)
                .click(function () {
                    likeMessage(message.id, message.messageLikes.length === 0);
                })
                .mouseover(function () {
                    if(message.messageReads.length === 0) {
                        if (!message.messageReads.includes(meUserId)) {
                            readMessage(message.id);
                        }
                    }
                })
        }
        else {
            $("#" + message.id)
                .click(function () {
                    deleteMessage(message.id)
                })
                .mouseover(function () {
                    messageToChangeId = message.id
                    messageText = message.text
                    changeCurrentData()
                })
        }
    })
}

$(function () {
    $("form").on('submit', e => {
        e.preventDefault();
    });
    $( "#connect" ).click(function() { connect(); });
    $( "#disconnect" ).click(function() { disconnect(); });
    $( "#send" ).click(function() { sendText(); });
    $( "#update" ).click(function() { updateMessage(); });
});