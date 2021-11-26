package com.zestic.coin.bouncycastle.asn1.cms;

import com.zestic.coin.bouncycastle.asn1.ASN1SequenceParser;
import com.zestic.coin.bouncycastle.asn1.ASN1TaggedObjectParser;
import com.zestic.coin.bouncycastle.asn1.DEREncodable;
import com.zestic.coin.bouncycastle.asn1.DERObjectIdentifier;

import java.io.IOException;

/*
 * Produce an object suitable for an ASN1OutputStream.
 * <pre>
 * ContentInfo ::= SEQUENCE {
 *          contentType ContentType,
 *          content
 *          [0] EXPLICIT ANY DEFINED BY contentType OPTIONAL }
 * </pre>
 */
public class ContentInfoParser {

    private final DERObjectIdentifier contentType;
    private final ASN1TaggedObjectParser content;

    public ContentInfoParser(
            ASN1SequenceParser seq)
            throws IOException {
        contentType = (DERObjectIdentifier) seq.readObject();
        content = (ASN1TaggedObjectParser) seq.readObject();
    }

    public DERObjectIdentifier getContentType() {
        return contentType;
    }

    public DEREncodable getContent(
            int tag)
            throws IOException {
        if (content != null) {
            return content.getObjectParser(tag, true);
        }

        return null;
    }
}
