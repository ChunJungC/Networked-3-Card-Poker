package cchiu27.project3.client.network;

import cchiu27.project3.server.model.PokerInfo;

public interface PokerInfoListener {
    void onPokerInfo(PokerInfo info);
    void onConnectionClosed(String reason);
}
