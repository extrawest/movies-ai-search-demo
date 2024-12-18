import {useState} from "react";
import {AssistantService} from "Frontend/generated/endpoints";
import {MessageInput} from "@vaadin/react-components/MessageInput";
import {SplitLayout} from "@vaadin/react-components/SplitLayout";
import {MessageItem} from "../components/Message";
import MessageList from "Frontend/components/MessageList";

export default function Index() {
  const [working, setWorking] = useState(false);
  const [messages, setMessages] = useState<MessageItem[]>([{
    role: 'assistant',
    content: 'Welcome to Movies Search Demo! How can I help you?'
  }]);

  function addMessage(message: MessageItem) {
    setMessages(messages => [...messages, message]);
  }

  function appendToLatestMessage(chunk: string) {
    setMessages(messages => {
      const latestMessage = messages[messages.length - 1];
      latestMessage.content += chunk;
      return [...messages.slice(0, -1), latestMessage];
    });
  }

  async function sendMessage(message: string) {
    setWorking(true);
    addMessage({
      role: 'user',
      content: message
    });
    let first = true;
    AssistantService.chat(message)
      .onNext(token => {
        if (first && token) {
          addMessage({
            role: 'assistant',
            content: token
          });

          first = false;
        } else {
          appendToLatestMessage(token);
        }
      })
      .onError(() => setWorking(false))
      .onComplete(() => setWorking(false));
  }


  return (
    <SplitLayout className="center h-full">
      <div className="flex flex-col gap-m p-m box-border h-full" style={{maxWidth: '80%', border: '1px solid #EEE'}}>
        <h3>LangChain4j Neo4j Graph RAG - Movies Search Demo</h3>
        <MessageList messages={messages} className="flex-grow"/>
        <MessageInput onSubmit={e => sendMessage(e.detail.value)} className="px-0"/>
      </div>
    </SplitLayout>

  );
}
