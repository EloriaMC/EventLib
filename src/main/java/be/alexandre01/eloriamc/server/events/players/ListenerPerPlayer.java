package be.alexandre01.eloriamc.server.events.players;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class ListenerPerPlayer {
    @Getter
    ArrayList<IPlayerEvent> listeners = new ArrayList<>();

    @Getter private Player player;

    public ListenerPerPlayer(Player player){
        this.player = player;
    }
}
