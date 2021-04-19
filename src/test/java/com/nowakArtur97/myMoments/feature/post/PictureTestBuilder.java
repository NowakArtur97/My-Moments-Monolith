package com.nowakArtur97.myMoments.feature.post;

import com.nowakArtur97.myMoments.testUtil.enums.ObjectType;

public class PictureTestBuilder {

    private Long id;

    private byte[] bytes = "image".getBytes();

    public PictureTestBuilder withId(Long id) {

        this.id = id;

        return this;
    }

    public PictureTestBuilder withBytes(byte[] bytes) {

        this.bytes = bytes;

        return this;
    }

    public Picture build(ObjectType type) {

        Picture picture;

        switch (type) {

            case ENTITY:

                picture = new PictureEntity(bytes);

                break;

            case MODEL:

                picture = new PictureModel(id, bytes);

                break;

            default:
                throw new RuntimeException("The specified type does not exist");
        }

        resetProperties();

        return picture;
    }

    private void resetProperties() {

        id = 1L;

        bytes = "image".getBytes();
    }
}
