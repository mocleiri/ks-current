package com.sigmasys.kuali.ksa.model.fm;

import com.sigmasys.kuali.ksa.model.AbstractGlBreakdownOverride;
import com.sigmasys.kuali.ksa.model.GlBreakdownOverrideType;

import javax.persistence.*;


/**
 * FM Manifest-aware GL Breakdown Override model.
 *
 * @author Michael Ivanov
 */
@Entity
@DiscriminatorValue(GlBreakdownOverrideType.MANIFEST_CODE)
public class ManifestGlBreakdownOverride extends AbstractGlBreakdownOverride {

    /**
     * Reference to Manifest
     */
    private FeeManagementManifest manifest;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FM_MANIFEST_ID_FK")
    public FeeManagementManifest getManifest() {
        return manifest;
    }

    public void setManifest(FeeManagementManifest manifest) {
        this.manifest = manifest;
    }
}
	


