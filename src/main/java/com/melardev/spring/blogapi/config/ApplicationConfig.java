package com.melardev.spring.blogapi.config;


import com.melardev.spring.blogapi.enums.AuthorizationPolicy;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConfigurationProperties("app")
public class ApplicationConfig {

    ValidationsConfig.ProductPolicies productPolicies;
    ValidationsConfig.CommentPolicies commentPolicies;
    RenderingConfig renderingConfig;

    public ValidationsConfig.CommentPolicies getCommentPolicies() {
        return commentPolicies;
    }

    public RenderingConfig getRenderingConfig() {
        return renderingConfig;
    }

    public void setRenderingConfig(RenderingConfig renderingConfig) {
        this.renderingConfig = renderingConfig;
    }

    public static class RenderingConfig {
        private int pageSize;

        public int getPageSize() {
            return pageSize;
        }

        public void setPageSize(int pageSize) {
            this.pageSize = pageSize;
        }
    }


    @ConfigurationProperties(prefix = "security.authorization")
    public static class ValidationsConfig {

        @ConfigurationProperties(prefix = "products.*")
        public static class ProductPolicies {
            private AuthorizationPolicy edit;
            private AuthorizationPolicy create;
            private AuthorizationPolicy delete;

            AuthorizationPolicy getCreatePolicy() {
                return getCreate();
            }

            AuthorizationPolicy getEditPolicy() {
                return getEdit();
            }

            AuthorizationPolicy getDeletePolicy() {
                return delete;
            }

            public AuthorizationPolicy getCreate() {
                return create;
            }

            public void setCreate(AuthorizationPolicy create) {
                this.create = create;
            }

            public AuthorizationPolicy getDelete() {
                return delete;
            }

            public void setDelete(AuthorizationPolicy create) {
                this.delete = create;
            }

            public AuthorizationPolicy getEdit() {
                return edit;
            }

            public void setEdit(AuthorizationPolicy edit) {
                this.edit = edit;
            }
        }

        public class CommentPolicies {
            private AuthorizationPolicy edit;
            private AuthorizationPolicy create;
            private AuthorizationPolicy delete;

            AuthorizationPolicy getCreatePolicy() {
                return getCreate();
            }

            AuthorizationPolicy getEditPolicy() {
                return getEdit();
            }

            AuthorizationPolicy getDeletePolicy() {
                return delete;
            }

            public AuthorizationPolicy getCreate() {
                return create;
            }

            public void setCreate(AuthorizationPolicy create) {
                this.create = create;
            }

            public AuthorizationPolicy getDelete() {
                return delete;
            }

            public void setDelete(AuthorizationPolicy create) {
                this.delete = create;
            }

            public AuthorizationPolicy getEdit() {
                return edit;
            }

            public void setEdit(AuthorizationPolicy edit) {
                this.edit = edit;
            }
        }
    }
}