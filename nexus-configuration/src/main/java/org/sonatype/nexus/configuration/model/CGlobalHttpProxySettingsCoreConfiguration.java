package org.sonatype.nexus.configuration.model;

import org.sonatype.configuration.validation.ValidationResponse;
import org.sonatype.nexus.configuration.application.ApplicationConfiguration;

public class CGlobalHttpProxySettingsCoreConfiguration
    extends AbstractCoreConfiguration
{
    public CGlobalHttpProxySettingsCoreConfiguration( ApplicationConfiguration applicationConfiguration )
    {
        super( applicationConfiguration );
    }

    @Override
    public CRemoteHttpProxySettings getConfiguration( boolean forWrite )
    {
        return (CRemoteHttpProxySettings) super.getConfiguration( forWrite );
    }

    @Override
    protected CRemoteHttpProxySettings extractConfiguration( Configuration configuration )
    {
        return configuration.getGlobalHttpProxySettings();
    }

    public void initConfig()
    {
        CRemoteHttpProxySettings newProxy = new CRemoteHttpProxySettings();

        getApplicationConfiguration().getConfigurationModel().setGlobalHttpProxySettings( newProxy );

        setOriginalConfiguration( newProxy );
    }

    public void nullifyConfig()
    {
        setOriginalConfiguration( null );

        setChangedConfiguration( null );
    }

    @Override
    public ValidationResponse doValidateChanges( Object changedConfiguration )
    {
        return new ValidationResponse();
    }
}
