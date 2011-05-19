package petriwars;

import gameserver.AbstractGameManager;
import gameserver.ClientManager;
import gameserver.ServerGram;

import java.util.ArrayList;
import java.util.logging.Logger;

public class PetriWarsGameManager extends AbstractGameManager {
	private static final Logger LOG = Logger.getLogger("PetriWars.GameManager");
	
	@Override public String getGameManagerName() {
		return "PetriWars";
	}

	@Override public void shutdownManager() {}

	@Override public void pushMessage(ClientManager cm, ServerGram sg) {
		
	}
	
	@Override public void addClient(ClientManager cm) {
		super.addClient(cm);
		
		final PetriWarsGame pwg = new PetriWarsGame(cm.getClientID());
		
		ArrayList<ClientManager> used = new ArrayList<ClientManager>();
		for (ClientManager c : freelist){
			pwg.addPlayer(c);
			used.add(c);
		}
		freelist.removeAll(used);
		
		pwg.setupGame();
		pwg.start();
	}

}
