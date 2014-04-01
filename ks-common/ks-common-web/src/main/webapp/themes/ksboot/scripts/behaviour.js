function initAMQListener() {
    var kualiSessionId = getKualiSessionId();
    var amq = org.activemq.Amq;
    amq.init({
        uri: 'amq',
        logging: true,
        timeout: 1,
        pollDelay: 1000,
        counter: 5,
        clientId: kualiSessionId
    });

    amq.addListener('theBrowser', 'org.kuali.student.user.message', function(msg) {
        if(msg.textContent == "org.kuali.student.user.message.stop"){
            amq.deactivate();
        } else {
            var res = displayMessage(msg);
        }

    }, { selector: "JMSCorrelationID='" + kualiSessionId + "'" });

}

function deactivateAMQListener(){
    org.activemq.Amq.deactivate();
}

function getKualiSessionId() {
    var kualiSessionId = document.cookie.match(/kualiSessionId=[^;]+/);

    if(kualiSessionId == null)
        return '';

    if(typeof(kualiSessionId) == 'undefined')
        return '';

    if(kualiSessionId.length <= 0)
        return '';

    kualiSessionId = kualiSessionId[0];

    var end = kualiSessionId.lastIndexOf(';');
    if(end == -1) end = kualiSessionId.length;

    return kualiSessionId.substring(15, end);
}

function displayMessage(msg){
    var index = msg.textContent.indexOf(":");
    var theme = msg.textContent.substring(0,index);
    var message = msg.textContent.substring(index+1);
    showGrowl(message, '', theme);
}