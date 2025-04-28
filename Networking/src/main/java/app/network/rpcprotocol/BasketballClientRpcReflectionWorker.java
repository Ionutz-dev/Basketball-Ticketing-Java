    package app.network.rpcprotocol;
    
    import app.model.Match;
    import app.model.Ticket;
    import app.model.User;
    import app.network.dto.DTOUtils;
    import app.network.dto.MatchDTO;
    import app.network.dto.TicketDTO;
    import app.network.dto.UserDTO;
    import app.services.BasketballException;
    import app.services.IBasketballObserver;
    import app.services.IBasketballServices;
    
    import java.io.ObjectInputStream;
    import java.io.ObjectOutputStream;
    import java.io.IOException;
    import java.lang.reflect.Method;
    import java.net.Socket;
    import java.util.stream.StreamSupport;
    
    public class BasketballClientRpcReflectionWorker implements Runnable, IBasketballObserver {
        private final IBasketballServices server;
        private final Socket connection;
    
        private ObjectInputStream input;
        private ObjectOutputStream output;
        private volatile boolean connected;

        private User user;

        public BasketballClientRpcReflectionWorker(IBasketballServices server, Socket connection) {
            this.server = server;
            this.connection = connection;
            try {
                output = new ObjectOutputStream(connection.getOutputStream());
                output.flush();
                input = new ObjectInputStream(connection.getInputStream());
                connected = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                while (connected) {
                    Request request = (Request) input.readObject();
                    Response response = handleRequest(request);
                    if (response != null) {
                        sendResponse(response);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                cleanup();
            }
        }

        private Response handleRequest(Request request) {
            String handlerName = "handle" + request.getType();
            System.out.println("[Worker] Handling request: " + handlerName);
            System.out.println("Handling via: " + handlerName);
            try {
                Method method = this.getClass().getDeclaredMethod(handlerName, Request.class);
                return (Response) method.invoke(this, request);
            } catch (Exception e) {
                e.printStackTrace();
                return new Response.Builder().type(ResponseType.ERROR).data("Error: " + e.getMessage()).build();
            }
        }
    
        private Response handleLOGIN(Request request) {
            String[] creds = (String[]) request.getData();
            try {
                User user = server.login(creds[0], creds[1], this);
                if (user != null) {
                    this.user = user;
                    return new Response.Builder().type(ResponseType.LOGIN_SUCCESS).data(DTOUtils.getDTO(user)).build();
                } else {
                    return new Response.Builder().type(ResponseType.LOGIN_FAILURE).data(null).build();
                }
            } catch (BasketballException e) {
                return new Response.Builder().type(ResponseType.ERROR).data("Login failed: " + e.getMessage()).build();
            }
        }
    
        private Response handleLOGOUT(Request request) {
            UserDTO userDTO = (UserDTO) request.getData();
            try {
                server.logout(DTOUtils.getFromDTO(userDTO));
                connected = false;
                return new Response.Builder().type(ResponseType.OK).data(null).build();
            } catch (BasketballException e) {
                return new Response.Builder().type(ResponseType.ERROR).data("Logout failed: " + e.getMessage()).build();
            }
        }
    
        private Response handleGET_AVAILABLE_MATCHES(Request request) {
            try {
                Iterable<Match> matches = server.getAvailableMatches();
                MatchDTO[] dtos = StreamSupport.stream(matches.spliterator(), false)
                        .map(DTOUtils::getDTO)
                        .toArray(MatchDTO[]::new);
                return new Response.Builder().type(ResponseType.MATCHES).data(dtos).build();
            } catch (BasketballException e) {
                return new Response.Builder().type(ResponseType.ERROR).data("Match fetch failed: " + e.getMessage()).build();
            }
        }
    
        private Response handleSELL_TICKET(Request request) {
            System.out.println("[Worker] handleSELL_TICKET called");
            Object data = request.getData();
            System.out.println("[Worker] Request data: " + data);
            if (!(data instanceof TicketDTO)) {
                return new Response.Builder().type(ResponseType.ERROR).data("Invalid data for SELL_TICKET").build();
            }
            TicketDTO ticketDTO = (TicketDTO) data;
            Ticket ticket = DTOUtils.getFromDTO(ticketDTO);
    
            try {
                server.sellTicket(ticket.getMatchId(), ticket.getUserId(), ticket.getCustomerName(), ticket.getSeatsSold());
                return new Response.Builder().type(ResponseType.TICKET_CONFIRMED).data(null).build();
            } catch (BasketballException e) {
                return new Response.Builder().type(ResponseType.ERROR).data("Sell failed: " + e.getMessage()).build();
            }
        }
    
        @Override
        public void ticketSoldUpdate() throws BasketballException {
            Response response = new Response.Builder()
                    .type(ResponseType.TICKET_SOLD)
                    .data(null)
                    .build();
            try {
                sendResponse(response);
            } catch (IOException e) {
                throw new BasketballException("Error sending TICKET_SOLD update", e);
            }
        }
    
        private void sendResponse(Response response) throws IOException {
            System.out.println("[Worker] Sending response: " + response.getType());
            synchronized (output) {
                output.writeObject(response);
                output.flush();
            }
        }

        private void cleanup() {
            try {
                if (user != null) {
                    System.out.println("[Worker] Cleaning up: logging out user " + user.getUsername());
                    server.logout(user);
                }
            } catch (Exception e) {
                System.err.println("Error logging out user during cleanup: " + e.getMessage());
            }

            try {
                if (input != null) input.close();
                if (output != null) output.close();
                if (connection != null) connection.close();
            } catch (IOException e) {
                System.err.println("Cleanup error: " + e.getMessage());
            }
        }
    }
