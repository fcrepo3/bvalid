package net.sf.bvalid.xsd;

import java.util.*;

import org.apache.log4j.Logger;

import org.apache.xerces.xni.grammars.Grammar;
import org.apache.xerces.xni.grammars.XMLGrammarDescription;
import org.apache.xerces.xni.grammars.XMLGrammarPool;

/**
 * An implementation of XMLGrammarPool that is keyed by source URL.
 *
 * Keying by location, rather than root element name or target namespace,
 * is necessary in order to support multiple (differing!) grammar definitions
 * in the same namespace.
 */
public class URLBasedGrammarPool implements XMLGrammarPool {

    private static Logger _LOG = Logger.getLogger(URLBasedGrammarPool.class.getName());

    /** Whether grammar caching is disabled. Defaults to false. */
    private boolean _locked;

    /** The pool, stored as a URL-to-Grammar map. */
    private Map _grammarMap;

    /** Our initial grammar set. */
    private static final Grammar[] _EMPTY_GRAMMAR_ARRAY = new Grammar[0];

    /**
     * Construct a <code>URLBasedGrammarPool</code>.
     */
    public URLBasedGrammarPool() {

        _locked = false;
        _grammarMap = new HashMap();
    }

    public Grammar[] retrieveInitialGrammarSet(String grammarType) {
        // Here we always return an empty array, which forces the 
        // validator to get each grammar via retrieveGrammar.  
        // This allows *us* to determine which grammar is 
        // appropriate on a case-by-case basis.
        _LOG.debug("Returning empty initial grammar set");
        return _EMPTY_GRAMMAR_ARRAY;
    }

    public void cacheGrammars(String grammarType, Grammar[] grammars) {

        synchronized (_grammarMap) {
            if (!_locked) {
                for (int i = 0; i < grammars.length; i++) {
                    String url = grammars[i].getGrammarDescription().getExpandedSystemId();
                    if (url != null) {
                        if (!_grammarMap.containsKey(url)) {
                            _grammarMap.put(url, grammars[i]);
                            _LOG.info("Put grammar in cache: " + url);
                        } else {
                            _LOG.debug("Grammar not put to cache; was already in map: " + url);
                        }
                    } else {
                        _LOG.debug("Grammar not put to cache; null expandedSystemId");
                    }
                }
            }
        }
    }

    public Grammar retrieveGrammar(XMLGrammarDescription desc) {

        String url = desc.getExpandedSystemId();
        if (url != null) {
            synchronized (_grammarMap) {
                Grammar grammar = (Grammar) _grammarMap.get(url);
                if (grammar != null) {
                    _LOG.info("Grammar retrieved from cache: " + url);
                    return grammar;
                } else {
                    _LOG.debug("Grammar not retrieved; not in cache: " + url);
                    return null;
                }
            }
        } else {
            _LOG.debug("Grammar not retrieved; null expandedSystemId");
            return null;
        }
        /*
        _LOG.debug("Trying to retrieve grammar with the following properties:\n"
                + "grammarType      : " + desc.getGrammarType() + "\n"
                + "baseSystemId     : " + desc.getBaseSystemId() + "\n"
                + "expandedSystemId : " + desc.getExpandedSystemId() + "\n"
                + "literalSystemId  : " + desc.getLiteralSystemId() + "\n"
                + "namespace        : " + desc.getNamespace() + "\n"
                + "publicId         : " + desc.getPublicId());
        return null;
        */
    }

    public void lockPool() {
        synchronized (_grammarMap) {
            _locked = true;
        }
    }

    public void unlockPool() {
        synchronized (_grammarMap) {
            _locked = false;
        }
    }

    public void clear() {
        synchronized (_grammarMap) {
            _grammarMap.clear();
        }
    }

}
