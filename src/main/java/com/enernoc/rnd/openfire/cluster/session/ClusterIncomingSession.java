package com.enernoc.rnd.openfire.cluster.session;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Collection;

import org.jivesoftware.openfire.session.IncomingServerSession;
import org.jivesoftware.openfire.session.Session;
import org.jivesoftware.util.cache.ClusterTask;
import org.jivesoftware.util.cache.ExternalizableUtil;

import com.enernoc.rnd.openfire.cluster.session.task.GetIncomingSessionTask;

public class ClusterIncomingSession extends ClusterSession implements
		IncomingServerSession {

	String streamID;
	String localDomain;
	Collection<String> validDomains = new ArrayList<String>();
	
	public ClusterIncomingSession( String streamID, byte[] nodeID ) {
		super( null, nodeID );
		this.streamID = streamID;
	}
	
	@Override
	void doCopy(Session s) {
		IncomingServerSession inS = (IncomingServerSession)s;
		this.localDomain =  inS.getLocalDomain();
		this.validDomains = inS.getValidatedDomains();

	}

	@Override
	void doReadExternal(ExternalizableUtil ext, ObjectInput in)
			throws IOException, ClassNotFoundException {
		this.streamID = ext.readSafeUTF(in);
		this.localDomain = ext.readSafeUTF(in);
		ext.readStrings(in, this.validDomains);
	}

	@Override
	void doWriteExternal(ExternalizableUtil ext, ObjectOutput out)
			throws IOException {
		ext.writeSafeUTF( out, streamID );
		ext.writeSafeUTF( out, localDomain );
		ext.writeStrings( out, validDomains );
	}

	@Override
	ClusterTask getSessionUpdateTask() {
		return new GetIncomingSessionTask( this.streamID );
	}

	public String getLocalDomain() {
		return this.localDomain;
	}

	public Collection<String> getValidatedDomains() {
		super.checkUpdate();
		return this.validDomains;
	}
}
