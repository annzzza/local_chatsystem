package com.insa.network;

import java.sql.Date;

public record TCPMessage (String content, User sender, User receiver, Date date) {}
